package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.response.ReviewProofDto;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewProofController {
	
	private final ReviewService reviewService;
	
	/** Owner: upload a proof file. Up to 3 per review, PENDING only. */
	@PostMapping(value = "/{reviewId}/proofs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ReviewProofDto> uploadProof(
		@PathVariable Long reviewId,
		@RequestParam("file") MultipartFile file,
		@AuthenticationPrincipal CustomUserDetails principal) {
		ReviewProofDto proof = reviewService.uploadProof(reviewId, file, principal.getId());
		return ResponseEntity.status(201).body(proof);
	}
	
	/** Owner or ADMIN: list proofs (metadata only, no URLs). */
	@GetMapping("/{reviewId}/proofs")
	public List<ReviewProofDto> listProofs(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		return reviewService.listProofs(reviewId, principal.getId(), isAdmin);
	}
	
	/** Owner or ADMIN: get a 15-minute presigned URL for a specific proof. */
	@GetMapping("/{reviewId}/proofs/{proofId}/url")
	public ResponseEntity<Map<String, String>> getProofUrl(
		@PathVariable Long reviewId,
		@PathVariable Long proofId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		String url = reviewService.getProofUrl(reviewId, proofId, principal.getId(), isAdmin);
		return ResponseEntity.ok(Map.of("url", url));
	}
	
	/** Owner or ADMIN: delete a proof. */
	@DeleteMapping("/{reviewId}/proofs/{proofId}")
	public ResponseEntity<Void> deleteProof(
		@PathVariable Long reviewId,
		@PathVariable Long proofId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		reviewService.deleteProof(reviewId, proofId, principal.getId(), isAdmin);
		return ResponseEntity.noContent().build();
	}
	
}