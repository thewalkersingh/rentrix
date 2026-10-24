package com.rentrix.rentrixserver.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateImageConfirmRequest {
	
	@NotBlank(message = "Key is required")
	@Size(max = 500, message = "Key too long")
	private String key;
	
	@NotNull(message = "Size is required")
	@Min(value = 1, message = "Size must have positive value")
	private Long sizeBytes;
	
	@NotNull(message = "Content type is required")
	@Size(max = 100, message = "Content type too long")
	private String contentType;
	
	@Size(max = 255, message = "Original filename too long")
	private String originalFilename;
	
}