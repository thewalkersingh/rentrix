package com.rentrix.rentrixserver.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateProofPresignRequest {
	
	@NotBlank(message = "Filename is required")
	@Size(max = 255, message = "Filename too long")
	private String filename;
	
	@NotBlank(message = "Content type is required")
	@Size(max = 100, message = "Content type too long")
	private String contentType;
	
	@NotNull(message = "Size is required")
	@Min(value = 1, message = "File cannot be empty")
	@Max(value = 20 * 1024 * 1024, message = "File too large (max 20 MB)")
	private Long sizeBytes;
	
}