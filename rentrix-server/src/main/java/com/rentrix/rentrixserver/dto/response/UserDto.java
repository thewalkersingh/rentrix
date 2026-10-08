package com.rentrix.rentrixserver.dto.response;
import lombok.Data;

@Data
public class UserDto {
	
	private Long id;
	private String name;
	private String email;
	public String role;
	
}