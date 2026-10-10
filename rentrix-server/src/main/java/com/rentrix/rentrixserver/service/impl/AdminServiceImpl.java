package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.ReviewProof;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.mapper.ReviewMapper;
import com.rentrix.rentrixserver.repository.FlatRepository;
import com.rentrix.rentrixserver.repository.ReviewProofRepository;
import com.rentrix.rentrixserver.repository.ReviewRepository;
import com.rentrix.rentrixserver.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Administrative review moderation service.
 * <p>
 * Responsibilities:
 * - Retrieve reviews by status
 * - Approve reviews
 * - Reject reviews
 * - Verify/unverify review proofs
 * - Auto-promote associated flats
 * <p>
 * Business Rules:
 * - Reviews cannot be reverted to PENDING
 * - Approved reviews verify associated proofs
 * - Rejected reviews unverify associated proofs
 * - First approved review can automatically make a flat visible
 * <p>
 * Security:
 * - Accessible only to ADMIN users through AdminController
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	
	private final ReviewRepository reviewRepository;
	private final FlatRepository flatRepository;
	private final ReviewProofRepository reviewProofRepository;
	
	@Override
	public PageResponse<ReviewDto> getReviewsByStatus(ReviewStatus status, Pageable pageable) {
		
		log.debug("Fetching reviews for moderation queue. status={}, page={}, size={}", status, pageable.getPageNumber(),
			pageable.getPageSize());
		
		return PageResponse.from(reviewRepository.findByStatus(status, pageable), ReviewMapper::toDto);
	}
	
	@Override
	@Transactional
	public ReviewDto moderate(Long reviewId, ReviewStatus status) {
		
		log.info("Review moderation requested. reviewId={}, targetStatus={}", reviewId, status);
		
		if (status == ReviewStatus.PENDING) {
			log.warn("Rejected illegal status transition. reviewId={}, targetStatus={}", reviewId, status);
			throw ApiException.badRequest("Cannot set status back to PENDING");
		}
		
		Review review = reviewRepository.findById(reviewId).orElseThrow(() -> {
			log.warn("Attempt to moderate non-existing review. reviewId={}", reviewId);
			return ApiException.notFound("Review not found");
		});
		
		ReviewStatus previousStatus = review.getStatus();
		log.debug("Review located. reviewId={}, currentStatus={}", reviewId, previousStatus);
		review.setStatus(status);
		List<ReviewProof> proofs = reviewProofRepository.findByReviewIdAndDeletedFalseOrderByDisplayOrderAsc(reviewId);
		
		if (proofs.isEmpty()) {
			log.warn("Review {} contains no active proofs", reviewId);
		}
		
		boolean verified = status == ReviewStatus.APPROVED;
		log.debug("Found {} proof(s) for review {}. Setting verified={}", proofs.size(), reviewId, verified);
		
		for (ReviewProof proof : proofs) {
			proof.setVerified(verified);
		}
		
		reviewProofRepository.saveAll(proofs);
		Review saved = reviewRepository.save(review);
		log.info("Review {} moderated successfully. {} -> {}", reviewId, previousStatus, status);
		
		if (status == ReviewStatus.APPROVED && !review.getFlat().getVisible()) {
			Flat flat = review.getFlat();
			flat.setVisible(true);
			flatRepository.save(flat);
			log.info("Flat {} auto-promoted to visible because review {} was approved", flat.getId(), reviewId);
		}
		
		log.info("Proof moderation completed. reviewId={}, proofsProcessed={}, verified={}", reviewId, proofs.size(),
			verified);
		
		return ReviewMapper.toDto(saved);
	}
	
}