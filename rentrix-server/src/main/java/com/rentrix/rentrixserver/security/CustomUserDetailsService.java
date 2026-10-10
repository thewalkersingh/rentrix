package com.rentrix.rentrixserver.security;

import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@NullMarked
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.debug("Loading user by email={}", email);
		User user = userRepository
							.findByEmail(email)
							.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		return new CustomUserDetails(user);
	}
	
	public UserDetails loadUserById(Long id) {
		log.debug("Loading user by id={}", id);
		User user =
			userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
		return new CustomUserDetails(user);
	}
	
}