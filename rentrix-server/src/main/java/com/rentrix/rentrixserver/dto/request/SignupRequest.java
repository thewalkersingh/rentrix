package com.rentrix.rentrixserver.dto.request;

import com.rentrix.rentrixserver.entity.constants.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
	
	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 100, message = "Name must be 2-100 characters")
	private String name;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email is invalid")
	private String email;
	
	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 100, message = "Password must be at least 6 characters")
	private String password;
	
	@NotNull(message = "Role is required")
	private Role role;
	
}