package com.rentrix.rentrixserver.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateReviewRequest {
	
	@NotBlank(message = "Title is required")
	@Size(min = 3, max = 100, message = "Title must be 3-100 characters")
	private String title;
	
	@NotBlank(message = "Review content is required")
	@Size(min = 10, max = 2000, message = "Review must be 10-2000 characters")
	private String content;
	
	@NotNull(message = "Rating is required")
	@Min(value = 1, message = "Rating must be at least 1")
	@Max(value = 10, message = "Rating must be at most 10")
	private Integer rating;
	
}