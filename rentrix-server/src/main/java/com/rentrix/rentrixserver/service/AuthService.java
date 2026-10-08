package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.dto.request.LoginRequest;
import com.rentrix.rentrixserver.dto.request.SignupRequest;
import com.rentrix.rentrixserver.dto.response.AuthResponse;
import com.rentrix.rentrixserver.dto.response.UserDto;

public interface AuthService {
	
	AuthResponse signup(SignupRequest request);
	
	AuthResponse login(LoginRequest request);
	
	String refresh(String refreshToken);
	
	UserDto me(Long userId);
	
	void logout(Long userId);
	
}