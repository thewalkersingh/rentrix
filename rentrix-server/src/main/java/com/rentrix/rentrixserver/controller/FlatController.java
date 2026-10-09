package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatImageDto;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.FlatImageService;
import com.rentrix.rentrixserver.service.FlatService;
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

import java.util.List;

@RestController
@RequestMapping("/flats")
@RequiredArgsConstructor
public class FlatController {
	
	private final FlatService flatService;
	private final FlatImageService flatImageService;
	
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
		boolean isVerifiedCreator = principal.getRole().equals("LANDLORD") || principal.getRole().equals("ADMIN");
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