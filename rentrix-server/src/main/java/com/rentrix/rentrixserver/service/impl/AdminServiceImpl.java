package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.mapper.ReviewMapper;
import com.rentrix.rentrixserver.repository.FlatRepository;
import com.rentrix.rentrixserver.repository.ReviewRepository;
import com.rentrix.rentrixserver.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	
	private final ReviewRepository reviewRepository;
	private final FlatRepository flatRepository;
	
	@Override
	public PageResponse<ReviewDto> getReviewsByStatus(ReviewStatus status, Pageable pageable) {
		return PageResponse.from(reviewRepository.findByStatus(status, pageable), (ReviewMapper::toDto));
	}
	
	@Override
	@Transactional
	public ReviewDto moderate(Long reviewId, ReviewStatus status) {
		if (status == ReviewStatus.PENDING) {
			throw ApiException.badRequest("Cannot set status back to PENDING");
		}
		
		Review review = reviewRepository.findById(reviewId)
												  .orElseThrow(() -> ApiException.notFound("Review not found"));
		
		review.setStatus(status);
		
		// If approving a review with proof, mark the proof as verified
		if (status == ReviewStatus.APPROVED && review.getProofStorageKey() != null) {
			review.setProofVerified(true);
			log.info("Proof verified for review {}", reviewId);
		} else if (status == ReviewStatus.REJECTED) {
			// Rejecting removes verification but keeps the file for audit
			review.setProofVerified(false);
		}
		
		Review saved = reviewRepository.save(review);
		
		// Auto-promote flat if first approved review
		if (status == ReviewStatus.APPROVED && !review.getFlat().getVisible()) {
			Flat flat = review.getFlat();
			flat.setVisible(true);
			flatRepository.save(flat);
			log.info("Flat {} promoted to visible after first approved review", flat.getId());
		}
		
		log.info("Moderated review {} → {}", reviewId, status);
		return ReviewMapper.toDto(saved);
	}
	
}