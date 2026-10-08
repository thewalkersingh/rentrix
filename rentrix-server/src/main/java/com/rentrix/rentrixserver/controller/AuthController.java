package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.request.LoginRequest;
import com.rentrix.rentrixserver.dto.request.RefreshRequest;
import com.rentrix.rentrixserver.dto.request.SignupRequest;
import com.rentrix.rentrixserver.dto.response.AuthResponse;
import com.rentrix.rentrixserver.dto.response.UserDto;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest req) {
		return ResponseEntity.status(201).body(authService.signup(req));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
		return ResponseEntity.ok(authService.login(req));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest req) {
		String newAccess = authService.refresh(req.getRefreshToken());
		return ResponseEntity.ok(new RefreshResponse(newAccess));
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserDto> me(@AuthenticationPrincipal CustomUserDetails principal) {
		return ResponseEntity.ok(authService.me(principal.getId()));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails principal) {
		// If token already invalid, principal will be null — logout is a no-op either way
		if (principal != null) {
			authService.logout(principal.getId());
		}
		return ResponseEntity.noContent().build();
	}
	
	public record RefreshResponse(String accessToken) {
	
	}
	/*
	Note: RefreshResponse is a nested record — small, single-use, fine here. If you prefer a separate file, create
	dto/RefreshResponse.java.
	 */
}