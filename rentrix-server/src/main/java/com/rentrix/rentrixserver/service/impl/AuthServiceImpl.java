package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.dto.request.LoginRequest;
import com.rentrix.rentrixserver.dto.request.SignupRequest;
import com.rentrix.rentrixserver.dto.response.AuthResponse;
import com.rentrix.rentrixserver.dto.response.UserDto;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.entity.constants.Role;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.mapper.UserMapper;
import com.rentrix.rentrixserver.repository.UserRepository;
import com.rentrix.rentrixserver.security.JwtService;
import com.rentrix.rentrixserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	/** Roles a user can self-register as. ADMIN is intentionally excluded. */
	private static final Set<Role> SELF_SIGNUP_ROLES = EnumSet.of(Role.TENANT, Role.LANDLORD);
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	
	@Override
	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw ApiException.conflict("Email already registered");
		}
		
		if (!SELF_SIGNUP_ROLES.contains(request.getRole())) {
			throw ApiException.badRequest("Cannot self-register with role: " + request.getRole());
		}
		
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole());
		
		User saved = userRepository.save(user);
		log.info("User registered: {} ({})", saved.getEmail(), saved.getRole());
		
		return buildAuthResponse(saved);
	}
	
	@Override
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
										  .orElseThrow(() -> ApiException.unauthorized("Invalid credentials"));
		
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw ApiException.unauthorized("Invalid credentials");
		}
		
		log.info("User logged in: {}", user.getEmail());
		return buildAuthResponse(user);
	}
	
	@Override
	public String refresh(String refreshToken) {
		if (!jwtService.isValid(refreshToken)) {
			throw ApiException.unauthorized("Invalid or expired refresh token");
		}
		
		String type = jwtService.extractType(refreshToken);
		if (!"refresh".equals(type)) {
			throw ApiException.unauthorized("Invalid token type");
		}
		
		Long userId = jwtService.extractUserId(refreshToken);
		User user = userRepository.findById(userId)
										  .orElseThrow(() -> ApiException.unauthorized("User no longer exists"));
		
		return jwtService.generateAccessToken(
			user.getId(), user.getEmail(), user.getRole().name()
		);
	}
	
	@Override
	public UserDto me(Long userId) {
		User user = userRepository.findById(userId)
										  .orElseThrow(() -> ApiException.notFound("User not found"));
		return UserMapper.toDto(user);
	}
	
	@Override
	public void logout(Long userId) {
		// Stateless JWT — nothing to invalidate server-side.
		// Client discards tokens. Future: add refresh-token blacklist if needed.
		log.info("User logged out: {}", userId);
	}
	
	private AuthResponse buildAuthResponse(User user) {
		String accessToken = jwtService.generateAccessToken(
			user.getId(), user.getEmail(), user.getRole().name()
		);
		String refreshToken = jwtService.generateRefreshToken(
			user.getId(), user.getEmail(), user.getRole().name()
		);
		return new AuthResponse(UserMapper.toDto(user), accessToken, refreshToken);
	}
	
}