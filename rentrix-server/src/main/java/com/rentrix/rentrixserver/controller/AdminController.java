package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.ModerateReviewRequest;
import com.rentrix.rentrixserver.dto.PageResponse;
import com.rentrix.rentrixserver.dto.ReviewDto;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.service.AdminService;
import com.rentrix.rentrixserver.service.FlatService;
import com.rentrix.rentrixserver.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	private final AdminService adminService;
	private final ReviewService reviewService;
	private final FlatService flatService;
	
	// -- Direct Flat CRUD (admin override) --------------------------------
	@PostMapping("/flats/{id}/verify")
	public ResponseEntity<FlatResponse> verifyFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVerified(id, true));
	}
	
	@DeleteMapping("/flats/{id}/verify")
	public ResponseEntity<FlatResponse> unverifyFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVerified(id, false));
	}
	
	@PostMapping("/flats/{id}/visible")
	public ResponseEntity<FlatResponse> showFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVisible(id, true));
	}
	
	@DeleteMapping("/flats/{id}/visible")
	public ResponseEntity<FlatResponse> hideFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVisible(id, false));
	}
	
	// -- Direct review CRUD (admin override) --------------------------------
	@GetMapping("/reviews/{id}")
	public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
		return ResponseEntity.ok(reviewService.getReviewById(id));
	}
	
	@DeleteMapping("/reviews/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		reviewService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}
	
	// -- Moderation queue ----------------------------------------------------
	@GetMapping("/reviews")
	public PageResponse<ReviewDto> listReviews(@RequestParam(defaultValue = "PENDING") ReviewStatus status,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return adminService.getReviewsByStatus(status, pageable);
	}
	
	@PatchMapping("/reviews/{id}")
	public ResponseEntity<ReviewDto> moderateReview(@PathVariable Long id,
		@Valid @RequestBody ModerateReviewRequest req) {
		return ResponseEntity.ok(adminService.moderate(id, req.getStatus()));
	}
	
}
/*
Note:We are using @PreAuthorize("hasRole('ADMIN')") on the class — every method requires ADMIN. Combined with
.requestMatchers("/admin/**").hasRole("ADMIN") in SecurityConfig, this is double-Check.
 */