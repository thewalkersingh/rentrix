package com.rentrix.rentrixserver.mapper;

import com.rentrix.rentrixserver.dto.response.ReviewDto;
import com.rentrix.rentrixserver.dto.response.ReviewProofDto;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.ReviewProof;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;

import java.util.List;

public final class ReviewMapper {
	
	private ReviewMapper() {}
	
	public static ReviewDto toDto(Review review) {
		if (review == null) return null;
		ReviewDto dto = new ReviewDto();
		
		dto.setId(review.getId());
		dto.setTitle(review.getTitle());
		dto.setContent(review.getContent());
		dto.setRating(review.getRating());
		dto.setStatus(review.getStatus() != null ? review.getStatus().name() : null);
		dto.setCreatedAt(review.getCreatedAt());
		
		if (review.getUser() != null) {
			dto.setUserId(review.getUser().getId());
			dto.setUserName(review.getUser().getDisplayName());
		}
		if (review.getFlat() != null) {
			dto.setFlatId(review.getFlat().getId());
			dto.setFlatAddress(review.getFlat().getAddress() != null
										 ? review.getFlat().getAddress().getAddressLine()
										 : null);
		}
		
		// v0.1.3 — proof of living
		// Proofs
		List<ReviewProof> activeProofs = review.getProofs() == null
														? List.of()
														: review.getProofs().stream()
																  .filter(p -> !Boolean.TRUE.equals(p.getDeleted()))
																  .toList();
		
		boolean hasProof = !activeProofs.isEmpty();
		boolean anyVerified = activeProofs.stream()
													 .anyMatch(p -> Boolean.TRUE.equals(p.getVerified()));
		
		dto.setHasProof(hasProof);
		dto.setVerifiedStay(hasProof && anyVerified
									  && review.getStatus() == ReviewStatus.APPROVED);
		
		return dto;
	}
	
	public static ReviewDto toDto(Review review, String flatAddress) {
		ReviewDto dto = toDto(review);
		if (dto != null && flatAddress != null) {
			dto.setFlatAddress(flatAddress);
		}
		return dto;
	}
	
	public static ReviewProofDto toProofDto(ReviewProof proof) {
		if (proof == null) return null;
		return ReviewProofDto.builder()
									.id(proof.getId())
									.originalFilename(proof.getOriginalFilename())
									.contentType(proof.getContentType())
									.sizeBytes(proof.getSizeBytes())
									.displayOrder(proof.getDisplayOrder())
									.verified(proof.getVerified())
									.build();
	}
	
	public static List<ReviewProofDto> toProofDtos(List<ReviewProof> proofs) {
		if (proofs == null) return List.of();
		return proofs.stream().map(ReviewMapper::toProofDto).toList();
	}
	
}