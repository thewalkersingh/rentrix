package com.rentrix.rentrixserver.config;

import com.rentrix.rentrixserver.security.CustomUserDetailsService;
import com.rentrix.rentrixserver.security.JwtAuthenticationFilter;
import com.rentrix.rentrixserver.security.RestAccessDeniedHandler;
import com.rentrix.rentrixserver.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomUserDetailsService userDetailsService;
	private final RestAuthenticationEntryPoint authenticationEntryPoint;
	private final RestAccessDeniedHandler accessDeniedHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			 .csrf(AbstractHttpConfigurer::disable)
			 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			 .exceptionHandling(
				 ex -> ex.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler))
			 .authorizeHttpRequests(
				 auth -> auth
								// Public: auth
								.requestMatchers("/auth/signup", "/auth/login", "/auth/refresh").permitAll()
								
								// Public: browse flats + read reviews
								.requestMatchers(HttpMethod.GET, "/flats/me").authenticated()     // ← first
								.requestMatchers(HttpMethod.GET, "/flats/*/reviews").permitAll()
								.requestMatchers(HttpMethod.GET, "/flats/**").permitAll()
								
								// Public: docs + H2 console (dev only — remove before prod!)
								.requestMatchers("/v3/api-docs/**",
									"/swagger-ui/**",
									"/swagger-ui.html",
									"/swagger-resources/**",
									"/webjars/**",
									"/h2-console/**").permitAll()
								.requestMatchers("/actuator/health").permitAll()
								
								// Admin-only
								.requestMatchers("/admin/**").hasRole("ADMIN")
								
								// Everything else requires auth
								.anyRequest().authenticated())
			 
			 // H2 console uses frames — allow same-origin
			 .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
			 .authenticationProvider(authenticationProvider())
			 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	/**
	 * CORS beans — duplicates WebConfig, but Security needs its own CorsConfigurationSource.
	 * The WebConfig is still used for non-secured endpoints; this one wins for secured ones.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(
			List.of("http://localhost:5173", "https://myrentrix.vercel.app",
				"https://*.vercel.app"          // ← covers every preview URL
			));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
}