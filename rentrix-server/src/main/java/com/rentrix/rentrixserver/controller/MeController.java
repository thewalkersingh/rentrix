package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class MeController {
	
	private final ReviewService reviewService;
	
	@GetMapping("/reviews")
	public PageResponse<ReviewDto> getMyReviews(
		@AuthenticationPrincipal CustomUserDetails principal,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
		Pageable pageable) {
		
		return reviewService.getMyReviews(principal.getId(), pageable);
		
	}
	
}