package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.CreateImageConfirmRequest;
import com.rentrix.rentrixserver.dto.request.CreateImagePresignRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.*;
import com.rentrix.rentrixserver.entity.constants.Role;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.FlatImageService;
import com.rentrix.rentrixserver.service.FlatService;
import com.rentrix.rentrixserver.service.PresignedUploadService;
import com.rentrix.rentrixserver.service.RateLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

/**
 * Flat management APIs.
 * <p>
 * Responsibilities:
 * - Public flat search
 * - Flat detail retrieval
 * - Flat creation
 * - Flat updates
 * - Flat deletion
 * - Flat image management
 * <p>
 * Access Rules:
 * - Listing and detail views are public
 * - Creation requires authentication
 * - Updates require owner, creator, or admin
 * - Image management requires owner, creator, or admin
 */
@RestController
@RequestMapping("/flats")
@RequiredArgsConstructor
public class FlatController {
	
	private final FlatService flatService;
	private final FlatImageService flatImageService;
	private final PresignedUploadService presignedUploadService;
	private final RateLimitService rateLimitService;
	
	/** Public: list flats with optional filters. */
	@GetMapping
	public PageResponse<FlatSummaryResponse> listFlats(@ModelAttribute FlatFilterRequest filter,
		@PageableDefault(size = 9, sort = "rent", direction = Sort.Direction.ASC) Pageable pageable) {
		
		return flatService.listFlats(filter, pageable);
	}
	
	/**
	 * GET /flats/me must be declared before GET /flats/{id} or Spring will try to parse "me" as an ID and 400.
	 * The order in the class matters. Put /me above /{id}.
	 */
	@GetMapping("/me")
	public PageResponse<FlatSummaryResponse> listMyFlats(@AuthenticationPrincipal CustomUserDetails principal,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return flatService.listMyFlats(principal.getId(), pageable);
	}
	
	/** Public: flat detail. */
	@GetMapping("/{id}")
	public ResponseEntity<FlatResponse> getFlatById(@PathVariable Long id) {
		return ResponseEntity.ok(flatService.getFlatById(id));
	}
	
	/** Any Authenticated User: create a flat. */
	@PostMapping
	public ResponseEntity<FlatResponse> createFlat(@Valid @RequestBody CreateFlatRequest request,
		@AuthenticationPrincipal CustomUserDetails principal) {
		Role role = Role.valueOf(principal.getRole());
		boolean isVerifiedCreator = "LANDLORD".equals(principal.getRole())
												 || "ADMIN".equals(principal.getRole());
		FlatResponse created = flatService.createFlat(request, principal.getId(), isVerifiedCreator);
		
		return ResponseEntity.status(201).body(created);
	}
	
	/** Owner or ADMIN: partial update. */
	@PatchMapping("/{id}")
	public ResponseEntity<FlatResponse> updateFlat(@PathVariable Long id,
		@Valid @RequestBody UpdateFlatRequest request,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		return ResponseEntity.ok(flatService.updateFlat(id, request, principal.getId(), isAdmin));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFlat(@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		flatService.deleteFlat(id, principal.getId(), isAdmin);
		return ResponseEntity.noContent().build();
	}
	
	/** Public: list images for a flat. */
	@GetMapping("/{id}/images")
	public List<FlatImageDto> listImages(@PathVariable Long id) {
		return flatImageService.listImages(id);
	}
	
	/** Owner / creator / ADMIN: request a presigned URL for direct S3 image upload. */
	@PostMapping("/{id}/images/presigned-url")
	public ResponseEntity<PresignedUploadResponse> presignImageUpload(@PathVariable Long id,
		@Valid @RequestBody CreateImagePresignRequest req, @AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		flatImageService.assertCanManageFlat(id, principal.getId(), isAdmin);
		PresignedUploadResponse response = presignedUploadService.presignImageUpload(
			id, req.getFilename(), req.getContentType(), req.getSizeBytes());
		rateLimitService.check(principal.getId(), "presign_image", 10, Duration.ofHours(1));
		return ResponseEntity.ok(response);
	}
	
	/** Owner / creator / ADMIN: confirm an uploaded image. */
	@PostMapping("/{id}/images/confirm")
	public ResponseEntity<FlatImageDto> confirmImageUpload(
		@PathVariable Long id,
		@Valid @RequestBody CreateImageConfirmRequest req,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		FlatImageDto image = flatImageService.confirmImageUpload(id, req, principal.getId(), isAdmin);
		return ResponseEntity.status(201).body(image);
	}
	
	/** Owner / creator / ADMIN: upload an image. */
	@PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FlatImageDto> uploadImage(
		@PathVariable Long id,
		@RequestParam("file") MultipartFile file,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		FlatImageDto uploaded = flatImageService.uploadImage(id, file, principal.getId(), isAdmin);
		return ResponseEntity.status(201).body(uploaded);
	}
	
	/** Owner / creator / ADMIN: delete an image. */
	@DeleteMapping("/{id}/images/{imageId}")
	public ResponseEntity<Void> deleteImage(
		@PathVariable Long id,
		@PathVariable Long imageId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		flatImageService.deleteImage(id, imageId, principal.getId(), isAdmin);
		return ResponseEntity.noContent().build();
	}
	
	/** Owner / creator / ADMIN: mark an image as primary. */
	@PatchMapping("/{id}/images/{imageId}/primary")
	public ResponseEntity<FlatImageDto> setPrimaryImage(
		@PathVariable Long id,
		@PathVariable Long imageId,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		FlatImageDto image = flatImageService.setPrimary(id, imageId, principal.getId(), isAdmin);
		return ResponseEntity.ok(image);
	}
	
}