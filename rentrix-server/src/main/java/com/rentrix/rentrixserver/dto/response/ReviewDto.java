package com.rentrix.rentrixserver.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

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
	private LocalDateTime createdAt;
	// v0.1.3 — proof of living
	// true if review APPROVED and has ≥1 verified proof
	private Boolean verifiedStay;
	// true if review has ≥1 uploaded proof (any state)
	private Boolean hasProof;
	
}