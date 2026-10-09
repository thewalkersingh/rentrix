package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewProofController {
	
	private final ReviewService reviewService;
	
	@PostMapping(value = "/{reviewId}/proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> uploadProof(
		@PathVariable Long reviewId,
		@RequestParam("file") MultipartFile file,
		@AuthenticationPrincipal CustomUserDetails principal) {
		reviewService.uploadProof(reviewId, file, principal.getId());
		return ResponseEntity.status(201).build();
	}
	
	@GetMapping("/{reviewId}/proof-url")
	public ResponseEntity<Map<String, String>> getProofUrl(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		String url = reviewService.getProofUrl(reviewId, principal.getId(), isAdmin);
		return ResponseEntity.ok(Map.of("url", url));
	}
	
	@DeleteMapping("/{reviewId}/proof")
	public ResponseEntity<Void> deleteProof(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		reviewService.deleteProof(reviewId, principal.getId(), isAdmin);
		return ResponseEntity.noContent().build();
	}
	
}