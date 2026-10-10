package com.rentrix.rentrixserver.security;

import com.rentrix.rentrixserver.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;
	private final ObjectMapper objectMapper;
	
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
		
		// No header then anonymous request, let Security decide
		if (authHeader == null || authHeader.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// Header present but not a Bearer scheme then reject (client tried to auth badly)
		if (!authHeader.startsWith("Bearer")) {
			writeUnauthorized(response, "Invalid Authorization header");
			return;
		}
		
		final String token = authHeader.substring("Bearer".length()).trim();
		
		// Header said Bearer but no token then reject
		if (token.isEmpty()) {
			writeUnauthorized(response, "Missing bearer token");
			return;
		}
		
		// Validate the token — malformed / expired / bad signature all fail here
		if (!jwtService.isValid(token)) {
			writeUnauthorized(response, "Invalid or expired token");
			return;
		}
		
		// Ensuring if it's an access token, not a refresh token
		String type = jwtService.extractType(token);
		if (!"access".equals(type)) {
			writeUnauthorized(response, "Invalid token type");
			return;
		}
		
		// If already authenticated don't overwrite
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			Long userId = jwtService.extractUserId(token);
			UserDetails userDetails = userDetailsService.loadUserById(userId);
			
			UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
			log.debug("JWT authenticated userId={}", userId);
			filterChain.doFilter(request, response);
		} catch (Exception ex) {
			log.warn("Authentication failed for JWT user lookup");
			log.debug("Authentication failure details", ex);
			SecurityContextHolder.clearContext();
			writeUnauthorized(response, "Invalid token");
		}
	}
	
	private void writeUnauthorized(HttpServletResponse response, String message)
		throws IOException {
		SecurityContextHolder.clearContext();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(
			response.getOutputStream(),
			new ErrorResponse(message, 401)
		);
	}
	
}