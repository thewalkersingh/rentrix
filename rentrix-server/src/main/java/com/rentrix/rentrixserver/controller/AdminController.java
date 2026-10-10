package com.rentrix.rentrixserver.controller;
import com.rentrix.rentrixserver.dto.request.ModerateReviewRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
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

/**
 * Administrative operations for managing flats and reviews.
 * <p>
 * Access Control:
 * - All endpoints require ADMIN role.
 * - Enforcement happens at:
 * 1. SecurityConfig via request matcher
 * 2. @PreAuthorize at controller level
 * <p>
 * This provides defense-in-depth and prevents accidental exposure.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	private final AdminService adminService;
	private final ReviewService reviewService;
	private final FlatService flatService;
	
	/**
	 * Marks a flat as verified.
	 * <p>
	 * Verified flats are considered reviewed and approved
	 * by platform administrators and may be shown with
	 * a verification badge.
	 *
	 * @param id Flat ID
	 *
	 * @return Updated flat details
	 */
	@PostMapping("/flats/{id}/verify")
	public ResponseEntity<FlatResponse> verifyFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVerified(id, true));
	}
	
	/**
	 * Removes verification from a flat.
	 *
	 * @param id Flat ID
	 *
	 * @return Updated flat details
	 */
	@PatchMapping("/flats/{id}/verify")
	public ResponseEntity<FlatResponse> unverifyFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVerified(id, false));
	}
	
	/**
	 * Makes a flat visible in public listings.
	 *
	 * @param id Flat ID
	 *
	 * @return Updated flat details
	 */
	@PostMapping("/flats/{id}/visible")
	public ResponseEntity<FlatResponse> showFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVisible(id, true));
	}
	
	/**
	 * Hides a flat from public listings without deleting it.
	 * <p>
	 * Useful for moderation and policy enforcement.
	 *
	 * @param id Flat ID
	 *
	 * @return Updated flat details
	 */
	@PatchMapping("/flats/{id}/visible")
	public ResponseEntity<FlatResponse> hideFlat(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.setVisible(id, false));
	}
	
	/**
	 * Retrieves review details by ID.
	 * <p>
	 * Admins can view reviews regardless of status.
	 *
	 * @param id Review ID
	 *
	 * @return Review details
	 */
	@GetMapping("/reviews/{id}")
	public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
		return ResponseEntity.ok(reviewService.getReviewById(id));
	}
	
	/**
	 * Deletes a review.
	 * <p>
	 * Depending on implementation this may perform
	 * either soft delete or permanent deletion.
	 *
	 * @param id Review ID
	 *
	 * @return Success message
	 */
	@DeleteMapping("/reviews/{id}")
	public ResponseEntity<String> deleteReview(@PathVariable Long id) {
		return ResponseEntity.ok(reviewService.deleteReview(id));
	}
	
	/**
	 * Moderation queue for reviews.
	 * <p>
	 * Default status is PENDING.
	 * <p>
	 * Supports:
	 * - Pagination
	 * - Sorting
	 * - Status filtering
	 * <p>
	 * Example:
	 * GET /admin/reviews?status=PENDING&page=0&size=20
	 *
	 * @param status   Review status filter
	 * @param pageable Pagination configuration
	 *
	 * @return Paginated reviews
	 */
	@GetMapping("/reviews")
	public PageResponse<ReviewDto> listReviews(
		@RequestParam(defaultValue = "PENDING") ReviewStatus status,
		@PageableDefault(
			size = 20,
			sort = "createdAt",
			direction = Sort.Direction.DESC
		) Pageable pageable) {
		
		return adminService.getReviewsByStatus(status, pageable);
	}
	
	/**
	 * Moderates a review.
	 * <p>
	 * Allowed transitions:
	 * - PENDING -> APPROVED
	 * - PENDING -> REJECTED
	 * <p>
	 * If proof uploads are present:
	 * - APPROVED marks proof(s) verified
	 * - REJECTED removes verification state
	 *
	 * @param id  Review ID
	 * @param req Moderation request
	 *
	 * @return Updated review
	 */
	@PatchMapping("/reviews/{id}")
	public ResponseEntity<ReviewDto> moderateReview(
		@PathVariable Long id,
		@Valid @RequestBody ModerateReviewRequest req) {
		
		return ResponseEntity.ok(
			adminService.moderate(id, req.getStatus())
		);
	}
	
}