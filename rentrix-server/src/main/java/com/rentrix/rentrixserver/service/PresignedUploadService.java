package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.dto.response.PresignedUploadResponse;

public interface PresignedUploadService {
	
	/**
	 * Generates a presigned POST for a flat image.
	 * Enforces content-type, max size (10 MB), and a locked S3 key prefix.
	 */
	PresignedUploadResponse presignImageUpload(Long flatId, String filename, String contentType, long sizeBytes);
	
	/**
	 * Generates a presigned POST for a proof document.
	 * Enforces content-type, max size (20 MB), private bucket.
	 */
	PresignedUploadResponse presignProofUpload(Long reviewId, String filename, String contentType, long sizeBytes);
	
	/**
	 * Verifies an uploaded object exists in the expected bucket and matches
	 * the declared size and content type. Returns true if valid.
	 */
	boolean verifyUploadedObject(String bucket, String key, long expectedSize, String expectedContentType);
	
	/**
	 * Deletes an object (used when confirm fails validation).
	 */
	void deleteObject(String bucket, String key);
	
}