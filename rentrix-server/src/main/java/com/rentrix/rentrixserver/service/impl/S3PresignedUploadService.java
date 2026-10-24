package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.config.StorageProperties;
import com.rentrix.rentrixserver.dto.response.PresignedUploadResponse;
import com.rentrix.rentrixserver.exception.FileValidationException;
import com.rentrix.rentrixserver.service.PresignedUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3PresignedUploadService implements PresignedUploadService {
	
	private static final Duration PRESIGN_TTL = Duration.ofMinutes(5);
	
	private final S3Client s3;
	private final S3Presigner s3Presigner;
	private final StorageProperties props;
	
	@Override
	public PresignedUploadResponse presignImageUpload(
		Long flatId, String filename, String contentType, long sizeBytes) {
		if (!props.getAllowedImageTypes().contains(contentType)) {
			throw new FileValidationException(
				"Unsupported image type. Allowed: " + props.getAllowedImageTypes());
		}
		
		long maxSize = props.getMaxImageSizeBytes();
		if (sizeBytes > maxSize) {
			throw new FileValidationException(
				"Image too large (max " + (maxSize / 1024 / 1024) + " MB)");
		}
		
		String ext = extensionFor(contentType);
		String key = "flats/" + flatId + "/" + UUID.randomUUID() + "." + ext;
		
		PutObjectRequest putRequest = PutObjectRequest.builder()
																	 .bucket(props.getPublicBucket())
																	 .key(key)
																	 .contentType(contentType)
																	 .build();
		
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
																							 .signatureDuration(PRESIGN_TTL)
																							 .putObjectRequest(putRequest)
																							 .build();
		
		PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
		
		String publicUrl = props.getPublicBaseUrl() + "/" + key;
		
		return PresignedUploadResponse.builder()
												.uploadUrl(presigned.url().toString())
												.key(key)
												.publicUrl(publicUrl)
												.expiresAt(Instant.now().plus(PRESIGN_TTL).toString())
												.build();
	}
	
	@Override
	public PresignedUploadResponse presignProofUpload(
		Long reviewId, String filename, String contentType, long sizeBytes) {
		if (!props.getAllowedProofTypes().contains(contentType)) {
			throw new FileValidationException(
				"Unsupported proof type. Allowed: " + props.getAllowedProofTypes());
		}
		
		long maxSize = props.getMaxProofSizeBytes();
		if (sizeBytes > maxSize) {
			throw new FileValidationException(
				"Proof too large (max " + (maxSize / 1024 / 1024) + " MB)");
		}
		
		String ext = extensionFor(contentType);
		String key = "reviews/" + reviewId + "/" + UUID.randomUUID() + "." + ext;
		
		PutObjectRequest putRequest = PutObjectRequest.builder()
																	 .bucket(props.getPrivateBucket())
																	 .key(key)
																	 .contentType(contentType)
																	 .build();
		
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
																							 .signatureDuration(PRESIGN_TTL)
																							 .putObjectRequest(putRequest)
																							 .build();
		
		PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
		
		return PresignedUploadResponse.builder()
												.uploadUrl(presigned.url().toString())
												.key(key)
												.publicUrl(null)
												.expiresAt(Instant.now().plus(PRESIGN_TTL).toString())
												.build();
	}
	
	@Override
	public boolean verifyUploadedObject(String bucket, String key, long expectedSize, String expectedContentType) {
		try {
			HeadObjectResponse head = s3.headObject(HeadObjectRequest.builder()
																						.bucket(bucket)
																						.key(key)
																						.build());
			
			if (head.contentLength() != expectedSize) {
				log.warn("Size mismatch for {}/{}. Expected {}, got {}",
					bucket, key, expectedSize, head.contentLength());
				return false;
			}
			
			if (!expectedContentType.equals(head.contentType())) {
				log.warn("Content type mismatch for {}/{}. Expected {}, got {}",
					bucket, key, expectedContentType, head.contentType());
				return false;
			}
			
			return true;
		} catch (NoSuchKeyException ex) {
			log.warn("Object not found for verification: {}/{}", bucket, key);
			return false;
		} catch (Exception ex) {
			log.error("Failed to verify object {}/{}", bucket, key, ex);
			return false;
		}
	}
	
	@Override
	public void deleteObject(String bucket, String key) {
		try {
			s3.deleteObject(DeleteObjectRequest.builder()
														  .bucket(bucket)
														  .key(key)
														  .build());
			log.info("Deleted object from S3: bucket={}, key={}", bucket, key);
		} catch (Exception ex) {
			log.warn("Failed to delete S3 object: bucket={}, key={}", bucket, key, ex);
		}
	}
	
	private String extensionFor(String contentType) {
		return switch (contentType) {
			case "image/jpeg" -> "jpg";
			case "image/png" -> "png";
			case "image/webp" -> "webp";
			case "application/pdf" -> "pdf";
			default -> "bin";
		};
	}
	
}