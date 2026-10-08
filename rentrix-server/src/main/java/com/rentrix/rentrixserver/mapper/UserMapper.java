package com.rentrix.rentrixserver.mapper;

import com.rentrix.rentrixserver.dto.response.UserDto;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.Role;

public final class UserMapper {
	
	private UserMapper() {}
	
	public static UserDto toDto(User user) {
		if (user == null) return null;
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setName(user.getDisplayName());
		dto.setRole(user.getRole() != null ? user.getRole().name() : null);
		return dto;
	}
	
	public static UserDto of(Long id, String name, String email, Role role) {
		UserDto dto = new UserDto();
		dto.setId(id);
		dto.setName(name);
		dto.setEmail(email);
		dto.setRole(role != null ? role.name() : null);
		return dto;
	}
	
}