package com.rentrix.rentrixserver.dto.request;

import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModerateReviewRequest {
	
	/**
	 * Only APPROVED or REJECTED are valid inputs.
	 * PENDING is the initial state and cannot be set via this endpoint.
	 */
	@NotNull(message = "Status is required")
	private ReviewStatus status;
	
}