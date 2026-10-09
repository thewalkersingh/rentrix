package com.rentrix.rentrixserver.dto.response;

import lombok.Data;

@Data
public class ReviewDto {
	
	private Long id;
	private Long userId;
	private String userName;
	private Long flatId;
	private String flatAddress;
	private String title;
	private String content;
	private Integer rating;
	private String status;
	
	// v0.1.3 — proof of living
	// true when proof uploaded AND verified
	private Boolean verifiedStay;
	// true when proof uploaded (regardless of verification)
	private Boolean hasProof;
	
}