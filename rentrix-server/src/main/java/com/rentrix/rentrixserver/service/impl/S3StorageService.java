package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.config.StorageProperties;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {
	
	private final S3Client s3;
	private final StorageProperties props;
	
	@Override
	public String uploadPublicImage(String prefix, String filename, byte[] content, String contentType) {
		String key = buildKey(prefix, filename);
		upload(props.getPublicBucket(), key, content, contentType, true);
		String url = publicUrlForKey(key);
		log.info("Uploaded public image: key={}, size={}B", key, content.length);
		return url;
	}
	
	@Override
	public String uploadPrivate(String prefix, String filename, byte[] content,
		String contentType, String originalFilename) {
		String key = buildKey(prefix, filename);
		upload(props.getPrivateBucket(), key, content, contentType, false);
		log.info("Uploaded private file: key={}, original={}, size={}B",
			key, originalFilename, content.length);
		return key;
	}
	
	@Override
	public void deletePublic(String key) {
		delete(props.getPublicBucket(), key);
	}
	
	@Override
	public void deletePrivate(String key) {
		delete(props.getPrivateBucket(), key);
	}
	
	@Override
	public String publicUrlForKey(String key) {
		String base = props.getPublicBaseUrl();
		if (base == null || base.isBlank()) {
			base = "https://" + props.getPublicBucket()
						 + ".s3." + props.getRegion() + ".amazonaws.com";
		}
		return base.replaceAll("/+$", "") + "/" + key;
	}
	
	@Override
	public String presignedPrivateUrl(String key, int expiryMinutes) {
		try {
			S3Presigner presigner = S3Presigner.builder()
														  .region(Region.of(props.getRegion()))
														  .credentialsProvider(credentialsProvider())
														  .build();
			
			GetObjectRequest getRequest = GetObjectRequest.builder()
																		 .bucket(props.getPrivateBucket())
																		 .key(key)
																		 .build();
			
			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
																								 .signatureDuration(
																									 Duration.ofMinutes(expiryMinutes))
																								 .getObjectRequest(getRequest)
																								 .build();
			
			PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
			return presigned.url().toString();
		} catch (Exception e) {
			log.error("Failed to generate presigned URL for key={}", key, e);
			throw ApiException.badRequest("Failed to generate download link");
		}
	}
	
	// ── Internal helpers ──────────────────────────────────────────────────
	private String buildKey(String prefix, String filename) {
		String cleanPrefix = prefix.replaceAll("^/+|/+$", "");
		return cleanPrefix + "/" + filename;
	}
	
	private void upload(String bucket, String key, byte[] content,
		String contentType, boolean cacheable) {
		try {
			PutObjectRequest.Builder req = PutObjectRequest.builder()
																		  .bucket(bucket)
																		  .key(key)
																		  .contentType(contentType)
																		  .contentLength((long) content.length);
			
			if (cacheable) {
				// Cache public images for 1 year — filenames are UUIDs, so immutable
				req.cacheControl("public, max-age=31536000, immutable");
			}
			
			s3.putObject(req.build(), RequestBody.fromBytes(content));
		} catch (Exception e) {
			log.error("S3 upload failed: bucket={}, key={}", bucket, key, e);
			throw ApiException.badRequest("Failed to upload file");
		}
	}
	
	private void delete(String bucket, String key) {
		try {
			s3.deleteObject(DeleteObjectRequest.builder()
														  .bucket(bucket)
														  .key(key)
														  .build());
			log.info("Deleted from S3: bucket={}, key={}", bucket, key);
		} catch (Exception e) {
			log.warn("S3 delete failed (non-fatal): bucket={}, key={}", bucket, key, e);
		}
	}
	
	// Returns an AWS credentials provider based on the configured access/secret keys, or the default provider chain if
	// not set.
	private AwsCredentialsProvider credentialsProvider() {
		if (props.getAccessKey() != null && !props.getAccessKey().isBlank()
				 && props.getSecretKey() != null && !props.getSecretKey().isBlank()) {
			return StaticCredentialsProvider.create(
				AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey()));
		}
		return DefaultCredentialsProvider.create();
	}
	
}