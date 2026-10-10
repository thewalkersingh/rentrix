package com.rentrix.rentrixserver.repository;

import com.rentrix.rentrixserver.entity.ReviewProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewProofRepository extends JpaRepository<ReviewProof, Long> {
	
	List<ReviewProof> findByReviewIdAndDeletedFalseOrderByDisplayOrderAsc(Long reviewId);
	
	Optional<ReviewProof> findByIdAndDeletedFalse(Long id);
	
	long countByReviewIdAndDeletedFalse(Long reviewId);
	
}