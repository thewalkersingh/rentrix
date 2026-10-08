package com.rentrix.rentrixserver.support;

import com.rentrix.rentrixserver.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTestHelper {
	
	@Autowired
	private JwtService jwtService;
	
	public String tokenFor(com.rentrix.rentrixserver.entity.User user) {
		return jwtService.generateAccessToken(
			user.getId(),
			user.getEmail(),
			user.getRole().name()
		);
	}
	
	public String bearer(com.rentrix.rentrixserver.entity.User user) {
		return "Bearer " + tokenFor(user);
	}
	
}