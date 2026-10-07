package com.rentrix.rentrixserver.mapper;

import com.rentrix.rentrixserver.dto.ReviewDto;
import com.rentrix.rentrixserver.entity.Review;

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
		
		if (review.getUser() != null) {
			dto.setUserId(review.getUser().getId());
			dto.setUserName(review.getUser().getUsername());
		}
		if (review.getFlat() != null) {
			dto.setFlatId(review.getFlat().getId());
		}
		
		return dto;
	}
	
	public static ReviewDto toDto(Review review, String flatAddress) {
		ReviewDto dto = toDto(review);
		if (dto != null && flatAddress != null) {
			dto.setFlatAddress(flatAddress);
		}
		return dto;
	}
	
}