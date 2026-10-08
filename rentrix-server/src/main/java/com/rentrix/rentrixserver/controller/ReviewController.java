package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.request.CreateReviewRequest;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flats/{flatId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
	
	private final ReviewService reviewService;
	
	/**
	 * Public: list APPROVED reviews for a flat.
	 * Returns Spring's Page JSON (content, totalElements, totalPages, number, size).
	 */
	@GetMapping
	public PageResponse<ReviewDto> getFlatReviews(@PathVariable Long flatId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		
		return reviewService.getReviewsByFlat(flatId, pageable);
		
	}
	
	/**
	 * Authenticated: create a review for a flat. Status defaults to PENDING.
	 */
	@PostMapping
	public ResponseEntity<ReviewDto> createReview(@PathVariable Long flatId,
		@AuthenticationPrincipal CustomUserDetails principal, @Valid @RequestBody CreateReviewRequest req) {
		
		ReviewDto created = reviewService.createReview(flatId, principal.getId(), req);
		return ResponseEntity.status(201).body(created);
	}
	
}