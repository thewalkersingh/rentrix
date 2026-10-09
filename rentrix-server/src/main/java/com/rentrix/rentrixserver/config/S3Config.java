package com.rentrix.rentrixserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {
	
	private final StorageProperties props;
	
	@Bean
	public S3Client s3Client() {
		AwsCredentialsProvider credentials;
		if (props.getAccessKey() != null && !props.getAccessKey().isBlank()
				 && props.getSecretKey() != null && !props.getSecretKey().isBlank()) {
			
			credentials = StaticCredentialsProvider
								  .create(AwsBasicCredentials.
												 create(props.getAccessKey(), props.getSecretKey()));
		} else {
			credentials = DefaultCredentialsProvider.create();
		}
		
		return S3Client.builder()
							.region(Region.of(props.getRegion()))
							.credentialsProvider(credentials)
							.build();
	}
	
}