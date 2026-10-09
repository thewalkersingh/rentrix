package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.dto.projection.RatingAggregate;
import com.rentrix.rentrixserver.dto.request.CreateReviewRequest;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.dto.response.ReviewDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReviewService {
	
	PageResponse<ReviewDto> getAllReviews(Pageable pageable);
	
	ReviewDto getReviewById(Long id);
	
	PageResponse<ReviewDto> getReviewsByFlat(Long flatId, Pageable pageable);
	
	ReviewDto createReview(Long flatId, Long userId, CreateReviewRequest req);
	
	PageResponse<ReviewDto> getMyReviews(Long userId, Pageable pageable);
	
	String deleteReview(Long id);
	
	/**
	 * Batch fetch rating aggregates for a set of flat IDs.
	 * Returns a map keyed by flat ID. Flats with no APPROVED reviews are absent.
	 */
	Map<Long, RatingAggregate> getRatingAggregates(List<Long> flatIds);
	
	/**
	 * Uploads a proof-of-living document for a review.
	 * Only the review owner may upload, and only while status is PENDING.
	 */
	void uploadProof(Long reviewId, MultipartFile file, Long requesterId);
	
	/**
	 * Generates a short-lived presigned URL to view a review's proof.
	 * Only review owner or ADMIN.
	 */
	String getProofUrl(Long reviewId, Long requesterId, boolean isAdmin);
	
	/**
	 * Deletes a review's proof (before approval).
	 */
	void deleteProof(Long reviewId, Long requesterId, boolean isAdmin);
	
}