package com.rentrix.rentrixserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
	
	@Value("${app.storage.s3.region}")
	private String region;
	
	@Value("${app.storage.s3.access-key:}")
	private String accessKey;
	
	@Value("${app.storage.s3.secret-key:}")
	private String secretKey;
	
	@Bean
	public S3Client s3Client() {
		AwsCredentialsProvider credentials;
		if (accessKey != null && !accessKey.isBlank()
				 && secretKey != null && !secretKey.isBlank()) {
			credentials = StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey));
		} else {
			// Fall back to env vars or IAM role (prod on Render/EC2)
			credentials = DefaultCredentialsProvider.create();
		}
		
		return S3Client.builder()
							.region(Region.of(region))
							.credentialsProvider(credentials)
							.build();
	}
	
}