package com.rentrix.rentrixserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.storage.s3")
public class StorageProperties {
	
	private String region;
	private String accessKey;
	private String secretKey;
	private String publicBucket;
	private String privateBucket;
	private String publicBaseUrl;
	private long maxImageSizeBytes = 10 * 1024 * 1024;
	private List<String> allowedImageTypes = List.of("image/jpeg", "image/png", "image/webp");
	
}