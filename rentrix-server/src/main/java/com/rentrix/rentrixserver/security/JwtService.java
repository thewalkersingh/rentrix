package com.rentrix.rentrixserver.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
	
	@Value("${app.jwt.secret}")
	private String secret;
	
	@Getter
	@Value("${app.jwt.access-token-expiry-ms}")
	private long accessTokenExpiryMs;
	
	@Value("${app.jwt.refresh-token-expiry-ms}")
	private long refreshTokenExpiryMs;
	
	private SecretKey signingKey;
	
	@PostConstruct
	void init() {
		if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException(
				"app.jwt.secret must be at least 32 bytes for HS256");
		}
		this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateAccessToken(Long userId, String email, String role) {
		return buildToken(userId, email, role, accessTokenExpiryMs, "access");
	}
	
	public String generateRefreshToken(Long userId, String email, String role) {
		return buildToken(userId, email, role, refreshTokenExpiryMs, "refresh");
	}
	
	private String buildToken(Long userId, String email, String role, long expiryMs, String type) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expiryMs);
		return Jwts.builder()
					  .subject(String.valueOf(userId))
					  .claim("email", email)
					  .claim("role", role)
					  .claim("type", type)
					  .issuedAt(now)
					  .expiration(exp)
					  .signWith(signingKey, Jwts.SIG.HS256)
					  .compact();
	}
	
	public Claims parseClaims(String token) {
		return Jwts.parser()
					  .verifyWith(signingKey)
					  .build()
					  .parseSignedClaims(token)
					  .getPayload();
	}
	
	public Long extractUserId(String token) {
		return Long.parseLong(parseClaims(token).getSubject());
	}
	
	public String extractEmail(String token) {
		return parseClaims(token).get("email", String.class);
	}
	
	public String extractRole(String token) {
		return parseClaims(token).get("role", String.class);
	}
	
	public String extractType(String token) {
		return parseClaims(token).get("type", String.class);
	}
	
	public boolean isValid(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date());
		} catch (Exception ex) {
			log.debug("JWT validation failed: {}", ex.getClass().getSimpleName());
			return false;
		}
	}
	
}