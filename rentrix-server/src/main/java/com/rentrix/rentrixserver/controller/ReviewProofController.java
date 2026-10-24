package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.request.CreateProofConfirmRequest;
import com.rentrix.rentrixserver.dto.request.CreateProofPresignRequest;
import com.rentrix.rentrixserver.dto.response.PresignedUploadResponse;
import com.rentrix.rentrixserver.dto.response.ReviewProofDto;
import com.rentrix.rentrixserver.entity.constants.Role;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.ReviewService;
import jakarta.validation.Valid;
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
		boolean isAdmin = Role.ADMIN.name().equals(principal.getRole());
		return reviewService.listProofs(reviewId, principal.getId(), isAdmin);
	}
	
	/** Owner or ADMIN: get a 15-minute presigned URL for a specific proof. */
	@GetMapping("/{reviewId}/proofs/{proofId}/url")
	public ResponseEntity<Map<String, String>> getProofUrl(
		@PathVariable Long reviewId,
		@PathVariable Long proofId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = Role.ADMIN.name().equals(principal.getRole());
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
	
	@PostMapping("/{reviewId}/proofs/presigned-url")
	public ResponseEntity<PresignedUploadResponse> presignProofUpload(
		@PathVariable Long reviewId,
		@Valid @RequestBody CreateProofPresignRequest req,
		@AuthenticationPrincipal CustomUserDetails principal) {
		// ownership check happens in service
		PresignedUploadResponse response = reviewService.presignProofUpload(
			reviewId, req.getFilename(), req.getContentType(), req.getSizeBytes());
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/{reviewId}/proofs/confirm")
	public ResponseEntity<ReviewProofDto> confirmProofUpload(
		@PathVariable Long reviewId,
		@Valid @RequestBody CreateProofConfirmRequest req,
		@AuthenticationPrincipal CustomUserDetails principal) {
		// ownership check happens in service
		ReviewProofDto proof = reviewService.confirmProofUpload(reviewId, req, principal.getId());
		return ResponseEntity.status(201).body(proof);
	}
	
}