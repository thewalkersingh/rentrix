package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.FlatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flats")
@RequiredArgsConstructor
public class FlatController {
	
	private final FlatService flatService;
	
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
	
	/** Owner or ADMIN: soft-delete. */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFlat(@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails principal) {
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		flatService.deleteFlat(id, principal.getId(), isAdmin);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/debug/h2-console")
	public Map<String, Object> debugH2(
		@Value("${spring.h2.console.enabled:NOT_SET}") String enabled,
		@Value("${spring.h2.console.path:NOT_SET}") String path) {
		return Map.of("enabled", enabled, "path", path);
	}
	
}