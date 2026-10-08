package com.rentrix.rentrixserver.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {
	
	@NotBlank(message = "refreshToken is required")
	private String refreshToken;
	
}