package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.dto.projection.RatingAggregate;
import com.rentrix.rentrixserver.dto.request.CreateReviewRequest;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
import com.rentrix.rentrixserver.dto.response.ReviewProofDto;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.ReviewProof;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.exception.FileValidationException;
import com.rentrix.rentrixserver.mapper.ReviewMapper;
import com.rentrix.rentrixserver.repository.FlatRepository;
import com.rentrix.rentrixserver.repository.ReviewProofRepository;
import com.rentrix.rentrixserver.repository.ReviewRepository;
import com.rentrix.rentrixserver.repository.UserRepository;
import com.rentrix.rentrixserver.service.DocumentProcessingService;
import com.rentrix.rentrixserver.service.ReviewService;
import com.rentrix.rentrixserver.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	
	private static final int MAX_PROOFS_PER_REVIEW = 3;
	
	private final ReviewRepository reviewRepository;
	private final ReviewProofRepository reviewProofRepository;
	private final FlatRepository flatRepository;
	private final UserRepository userRepository;
	private final StorageService storage;
	private final DocumentProcessingService documentProcessing;
	
	// ── Existing review methods (unchanged, only rebuild DTO with proofs) ──
	
	@Override
	public PageResponse<ReviewDto> getAllReviews(Pageable pageable) {
		return PageResponse.from(reviewRepository.findAll(pageable), ReviewMapper::toDto);
	}
	
	@Override
	public ReviewDto getReviewById(Long id) {
		Review review =
			reviewRepository.findById(id).orElseThrow(() -> ApiException.notFound("Review not found with id: " + id));
		return ReviewMapper.toDto(review);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageResponse<ReviewDto> getReviewsByFlat(Long flatId, Pageable pageable) {
		return PageResponse.from(reviewRepository.findByFlatIdAndStatus(flatId, ReviewStatus.APPROVED, pageable),
			ReviewMapper::toDto);
	}
	
	@Override
	@Transactional
	public ReviewDto createReview(Long flatId, Long userId, CreateReviewRequest req) {
		Flat flat =
			flatRepository.findById(flatId).orElseThrow(() -> ApiException.notFound("Flat not found with id: " + flatId));
		
		User user = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
		
		Review review = new Review();
		review.setFlat(flat);
		review.setUser(user);
		review.setTitle(req.getTitle());
		review.setContent(req.getContent());
		review.setRating(req.getRating());
		review.setStatus(ReviewStatus.PENDING);
		
		Review saved = reviewRepository.save(review);
		log.info("Review created: {} for flat {} by user {}", saved.getId(), flatId, userId);
		return ReviewMapper.toDto(saved);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageResponse<ReviewDto> getMyReviews(Long userId, Pageable pageable) {
		return PageResponse.from(reviewRepository.findByUserId(userId, pageable), ReviewMapper::toDto);
	}
	
	@Override
	@Transactional
	public String deleteReview(Long id) {
		if (!reviewRepository.existsById(id)) {
			throw ApiException.notFound("Review not found with id: " + id);
		}
		reviewRepository.deleteById(id);
		return "Review with id: " + id + " has been deleted";
	}
	
	@Override
	public Map<Long, RatingAggregate> getRatingAggregates(List<Long> flatIds) {
		if (flatIds == null || flatIds.isEmpty()) return Map.of();
		
		List<Object[]> rows = reviewRepository.findRatingAggregates(flatIds);
		Map<Long, RatingAggregate> result = new java.util.HashMap<>();
		for (Object[] row : rows) {
			Long flatId = ((Number) row[0]).longValue();
			Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : null;
			Long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;
			result.put(flatId, new RatingAggregate(avg, count));
		}
		return result;
	}
	
	// ── Proof-of-living ───────────────────────────────────────────────────
	
	@Override
	@Transactional
	public ReviewProofDto uploadProof(Long reviewId, MultipartFile file, Long requesterId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> ApiException.notFound("Review not found"));
		
		if (!review.getUser().getId().equals(requesterId)) {
			throw ApiException.forbidden("You can only upload proof for your own reviews");
		}
		
		if (review.getStatus() != ReviewStatus.PENDING) {
			throw ApiException.badRequest("Cannot upload proof after moderation");
		}
		
		long currentCount = reviewProofRepository.countByReviewIdAndDeletedFalse(reviewId);
		if (currentCount >= MAX_PROOFS_PER_REVIEW) {
			throw new FileValidationException("Maximum of " + MAX_PROOFS_PER_REVIEW + " proofs per review");
		}
		
		var processed = documentProcessing.process(file);
		
		String uuid = UUID.randomUUID().toString();
		String ext = "application/pdf".equals(processed.contentType()) ? "pdf" : "jpg";
		String filename = uuid + "." + ext;
		String prefix = "reviews/" + reviewId;
		
		String key = storage.uploadPrivate(prefix, filename, processed.bytes(), processed.contentType(),
			file.getOriginalFilename());
		
		ReviewProof proof = new ReviewProof();
		proof.setReview(review);
		proof.setStorageKey(key);
		proof.setOriginalFilename(file.getOriginalFilename());
		proof.setContentType(processed.contentType());
		proof.setSizeBytes((long) processed.bytes().length);
		proof.setDisplayOrder((int) currentCount);
		proof.setVerified(false);
		
		ReviewProof saved = reviewProofRepository.save(proof);
		log.info("Proof uploaded for review {}: id={}, key={}", reviewId, saved.getId(), key);
		
		return ReviewMapper.toProofDto(saved);
	}
	
	@Override
	public List<ReviewProofDto> listProofs(Long reviewId, Long requesterId, boolean isAdmin) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> ApiException.notFound("Review not found"));
		
		boolean isOwner = review.getUser().getId().equals(requesterId);
		if (!isAdmin && !isOwner) {
			throw ApiException.forbidden("You cannot view proofs for this review");
		}
		
		return ReviewMapper.toProofDtos(
			reviewProofRepository.findByReviewIdAndDeletedFalseOrderByDisplayOrderAsc(reviewId));
	}
	
	@Override
	public String getProofUrl(Long reviewId, Long proofId, Long requesterId, boolean isAdmin) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> ApiException.notFound("Review not found"));
		
		boolean isOwner = review.getUser().getId().equals(requesterId);
		if (!isAdmin && !isOwner) {
			throw ApiException.forbidden("You cannot view this proof");
		}
		
		ReviewProof proof = reviewProofRepository.findByIdAndDeletedFalse(proofId)
															  .orElseThrow(() -> ApiException.notFound("Proof not found"));
		
		if (!proof.getReview().getId().equals(reviewId)) {
			throw ApiException.notFound("Proof does not belong to this review");
		}
		
		return storage.presignedPrivateUrl(proof.getStorageKey(), 15);
	}
	
	@Override
	@Transactional
	public void deleteProof(Long reviewId, Long proofId, Long requesterId, boolean isAdmin) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> ApiException.notFound("Review not found"));
		
		boolean isOwner = review.getUser().getId().equals(requesterId);
		if (!isAdmin && !isOwner) {
			throw ApiException.forbidden("You cannot delete this proof");
		}
		
		if (review.getStatus() != ReviewStatus.PENDING && !isAdmin) {
			throw ApiException.badRequest("Cannot delete proof after moderation");
		}
		
		ReviewProof proof = reviewProofRepository.findByIdAndDeletedFalse(proofId)
															  .orElseThrow(() -> ApiException.notFound("Proof not found"));
		
		if (!proof.getReview().getId().equals(reviewId)) {
			throw ApiException.notFound("Proof does not belong to this review");
		}
		
		proof.setDeleted(true);
		reviewProofRepository.save(proof);
		storage.deletePrivate(proof.getStorageKey());
		
		log.info("Proof {} deleted from review {}", proofId, reviewId);
	}
	
}