package com.rentrix.rentrixserver.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.storage.s3")
public class StorageProperties {
	
	@NotBlank
	private String region;
	private String accessKey;
	private String secretKey;
	
	@NotBlank
	private String publicBucket;
	
	@NotBlank
	private String privateBucket;
	private String publicBaseUrl;
	private long maxImageSizeBytes = 10L * 1024 * 1024;
	private long maxProofSizeBytes = 15L * 1024 * 1024;
	private List<String> allowedImageTypes =
		List.of(
			"image/jpeg",
			"image/png",
			"image/webp"
		);
	
	private List<String> allowedProofTypes =
		List.of(
			"image/jpeg",
			"image/png",
			"image/webp",
			"application/pdf"
		);
	
}
/*
 Using @Getter and @Setter annotations from Lombok to generate getter and setter methods for the fields in the
 StorageProperties class to avoid toString() method generation for security reasons. The class is annotated with
 @Validated to enable validation of the properties, and @ConfigurationProperties to bind the properties from the
 application configuration file.
*/