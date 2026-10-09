package com.rentrix.rentrixserver.service;

public interface StorageService {
	
	/**
	 * Uploads a processed image to the public bucket.
	 *
	 * @param prefix      folder path within the bucket, e.g. "flats/123"
	 * @param filename    desired filename (usually a UUID), extension added automatically
	 * @param content     processed bytes (already stripped of EXIF)
	 * @param contentType MIME type of the processed bytes
	 *
	 * @return the public URL of the uploaded object
	 */
	String uploadPublicImage(String prefix, String filename, byte[] content, String contentType);
	
	/**
	 * Uploads a private document (proof-of-living) to the private bucket.
	 *
	 * @return the storage key (not a public URL — private bucket)
	 */
	String uploadPrivate(String prefix, String filename, byte[] content, String contentType, String originalFilename);
	
	/**
	 * Deletes an object from the public bucket by key.
	 */
	void deletePublic(String key);
	
	/**
	 * Deletes an object from the private bucket by key.
	 */
	void deletePrivate(String key);
	
	/**
	 * Returns the public URL for a stored key in the public bucket.
	 */
	String publicUrlForKey(String key);
	
	/**
	 * Generates a short-lived presigned URL for a private object.
	 *
	 * @param key           S3 key in the private bucket
	 * @param expiryMinutes how long the URL is valid
	 */
	String presignedPrivateUrl(String key, int expiryMinutes);
	
}