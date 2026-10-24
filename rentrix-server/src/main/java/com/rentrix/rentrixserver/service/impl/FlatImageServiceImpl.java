package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.config.StorageProperties;
import com.rentrix.rentrixserver.dto.request.CreateImageConfirmRequest;
import com.rentrix.rentrixserver.dto.response.FlatImageDto;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.FlatImage;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.exception.FileValidationException;
import com.rentrix.rentrixserver.mapper.FlatMapper;
import com.rentrix.rentrixserver.repository.FlatImageRepository;
import com.rentrix.rentrixserver.repository.FlatRepository;
import com.rentrix.rentrixserver.service.FlatImageService;
import com.rentrix.rentrixserver.service.ImageProcessingService;
import com.rentrix.rentrixserver.service.PresignedUploadService;
import com.rentrix.rentrixserver.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlatImageServiceImpl implements FlatImageService {
	
	private static final int MAX_IMAGES_PER_FLAT = 10;
	
	private final FlatRepository flatRepository;
	private final FlatImageRepository flatImageRepository;
	private final ImageProcessingService imageProcessing;
	private final StorageService storage;
	private final PresignedUploadService presignedUploadService;
	private final StorageProperties props;
	
	@Override
	@Transactional(readOnly = true)
	public List<FlatImageDto> listImages(Long flatId) {
		return FlatMapper.toImageDtos(
			flatImageRepository.findByFlatIdOrderByDisplayOrderAsc(flatId));
	}
	
	@Override
	@Transactional
	public FlatImageDto uploadImage(Long flatId, MultipartFile file, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(flatId)
										  .orElseThrow(() -> ApiException.notFound("Flat not found"));
		
		assertCanManage(flat, requesterId, isAdmin);
		
		long currentCount = flatImageRepository.countByFlatIdAndDeletedFalse(flatId);
		if (currentCount >= MAX_IMAGES_PER_FLAT) {
			throw new FileValidationException(
				"Maximum of " + MAX_IMAGES_PER_FLAT + " images per flat");
		}
		
		byte[] processed = imageProcessing.processImage(file);
		
		String uuid = UUID.randomUUID().toString();
		String filename = uuid + ".jpg";
		String prefix = "flats/" + flatId;
		
		String url = storage.uploadPublicImage(prefix, filename, processed, "image/jpeg");
		String key = prefix + "/" + filename;
		
		FlatImage image = new FlatImage();
		image.setFlat(flat);
		image.setStorageKey(key);
		image.setPublicUrl(url);
		image.setContentType("image/jpeg");
		image.setSizeBytes((long) processed.length);
		image.setOriginalFilename(file.getOriginalFilename());
		image.setDisplayOrder((int) currentCount);
		image.setIsPrimary(currentCount == 0);   // first image is primary
		
		FlatImage saved = flatImageRepository.save(image);
		log.info("Image uploaded. flatId={}, imageId={}, primary={}, sizeBytes={}",
			flatId, saved.getId(), saved.getIsPrimary(), saved.getSizeBytes());
		
		return FlatMapper.toImageDto(saved);
	}
	
	@Override
	@Transactional
	public void deleteImage(Long flatId, Long imageId, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(flatId)
										  .orElseThrow(() -> ApiException.notFound("Flat not found"));
		assertCanManage(flat, requesterId, isAdmin);
		
		FlatImage image = flatImageRepository.findByIdAndDeletedFalse(imageId)
														 .orElseThrow(() -> ApiException.notFound("Image not found"));
		
		if (!image.getFlat().getId().equals(flatId)) {
			throw ApiException.notFound("Image does not belong to this flat");
		}
		
		boolean wasPrimary =
			Boolean.TRUE.equals(image.getIsPrimary());
		
		image.setDeleted(true);
		image.setIsPrimary(false);
		
		flatImageRepository.save(image);
		
		storage.deletePublic(image.getStorageKey());
		
		if (wasPrimary) {
			promoteNextAsPrimary(flatId);
		}
		
		log.info("Deleted image {} from flat {}", imageId, flatId);
	}
	
	@Override
	@Transactional
	public FlatImageDto setPrimary(Long flatId, Long imageId, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(flatId)
										  .orElseThrow(() -> ApiException.notFound("Flat not found"));
		assertCanManage(flat, requesterId, isAdmin);
		
		FlatImage image = flatImageRepository.findByIdAndDeletedFalse(imageId)
														 .orElseThrow(() -> ApiException.notFound("Image not found"));
		
		if (!image.getFlat().getId().equals(flatId)) {
			throw ApiException.notFound("Image does not belong to this flat");
		}
		
		// Clear previous primary
		flatImageRepository.findByFlatIdAndIsPrimaryTrueAndDeletedFalse(flatId)
								 .ifPresent(prev -> {
									 prev.setIsPrimary(false);
									 flatImageRepository.save(prev);
								 });
		
		image.setIsPrimary(true);
		FlatImage saved = flatImageRepository.save(image);
		
		log.info("Set primary image {} for flat {}", imageId, flatId);
		return FlatMapper.toImageDto(saved);
	}
	
	@Override
	public void assertCanManageFlat(Long flatId, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(flatId)
										  .orElseThrow(() -> ApiException.notFound("Flat not found"));
		assertCanManage(flat, requesterId, isAdmin);
	}
	
	@Override
	@Transactional
	public FlatImageDto confirmImageUpload(Long flatId, CreateImageConfirmRequest req,
		Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(flatId)
										  .orElseThrow(() -> ApiException.notFound("Flat not found"));
		assertCanManage(flat, requesterId, isAdmin);
		
		// 1. Count check
		long currentCount = flatImageRepository.countByFlatIdAndDeletedFalse(flatId);
		if (currentCount >= MAX_IMAGES_PER_FLAT) {
			throw new FileValidationException(
				"Maximum of " + MAX_IMAGES_PER_FLAT + " images per flat");
		}
		
		// 2. Verify the object exists in S3
		boolean verified = presignedUploadService.verifyUploadedObject(
			props.getPublicBucket(), req.getKey(), req.getSizeBytes(), req.getContentType());
		if (!verified) {
			// Clean up the orphaned S3 object
			presignedUploadService.deleteObject(props.getPublicBucket(), req.getKey());
			throw new ApiException("Upload verification failed", HttpStatus.BAD_REQUEST);
		}
		
		// 3. Build FlatImage
		String publicUrl = props.getPublicBaseUrl() + "/" + req.getKey();
		
		FlatImage image = new FlatImage();
		image.setFlat(flat);
		image.setStorageKey(req.getKey());
		image.setPublicUrl(publicUrl);
		image.setContentType(req.getContentType());
		image.setSizeBytes(req.getSizeBytes());
		image.setOriginalFilename(req.getOriginalFilename());
		image.setDisplayOrder((int) currentCount);
		image.setIsPrimary(currentCount == 0);
		
		FlatImage saved = flatImageRepository.save(image);
		log.info("Image confirmed via presigned upload. flatId={}, imageId={}, key={}",
			flatId, saved.getId(), req.getKey());
		
		return FlatMapper.toImageDto(saved);
	}
	
	// ── Helpers ───────────────────────────────────────────────────────────
	private void assertCanManage(Flat flat, Long requesterId, boolean isAdmin) {
		boolean isOwner = flat.getOwner() != null && flat.getOwner().getId().equals(requesterId);
		boolean isCreator = flat.getCreatedBy() != null && flat.getCreatedBy().getId().equals(requesterId);
		if (!isAdmin && !isOwner && !isCreator) {
			throw ApiException.forbidden("You can only manage images for flats you own or created");
		}
	}
	
	private void promoteNextAsPrimary(Long flatId) {
		List<FlatImage> remaining =
			flatImageRepository.findByFlatIdOrderByDisplayOrderAsc(flatId);
		if (!remaining.isEmpty()) {
			FlatImage next = remaining.get(0);
			next.setIsPrimary(true);
			flatImageRepository.save(next);
			log.info("Promoted image {} to primary for flat {}", next.getId(), flatId);
		}
	}
	
}