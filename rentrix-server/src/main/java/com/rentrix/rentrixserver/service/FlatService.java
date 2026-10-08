package com.rentrix.rentrixserver.service;
import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface FlatService {
	
	// Public
	PageResponse<FlatSummaryResponse> listFlats(FlatFilterRequest filter, Pageable pageable);
	
	FlatResponse getFlatById(Long id);
	
	// Create — any authenticated user
	FlatResponse createFlat(CreateFlatRequest request, Long creatorId, boolean isVerifiedCreator);
	
	// Update — owner or ADMIN
	FlatResponse updateFlat(Long id, UpdateFlatRequest request, Long requesterId, boolean isAdmin);
	
	// Delete — owner, creator, or ADMIN
	void deleteFlat(Long id, Long requesterId, boolean isAdmin);
	
	// My flats — any authenticated user
	PageResponse<FlatSummaryResponse> listMyFlats(Long userId, Pageable pageable);
	
	// Admin
	FlatResponse setVerified(Long id, boolean verified);
	
	// Admin
	FlatResponse setVisible(Long id, boolean visible);
	
}