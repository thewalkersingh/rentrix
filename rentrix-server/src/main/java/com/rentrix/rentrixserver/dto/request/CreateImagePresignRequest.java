package com.rentrix.rentrixserver.dto.request;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateImagePresignRequest {
	
	@NotBlank(message = "Filename is required")
	@Size(max = 255, message = "Filename too long")
	private String filename;
	
	@NotBlank(message = "Content type is required")
	@Size(max = 100, message = "Content type too long")
	private String contentType;
	
	@NotBlank(message = "Size is required")
	@Min(value = 1, message = "File cannot be empty")
	@Max(value = 15 * 1024 * 1024, message = "File too large (max is 15 MB")
	private Long sizeBytes;
	
}