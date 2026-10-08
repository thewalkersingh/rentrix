package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import org.springframework.data.domain.Pageable;

public interface AdminService {
	
	PageResponse<ReviewDto> getReviewsByStatus(ReviewStatus status, Pageable pageable);
	
	ReviewDto moderate(Long reviewId, ReviewStatus status);
	
}