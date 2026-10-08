package com.rentrix.rentrixserver.repository;

import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	
	@EntityGraph(attributePaths = {"user", "user.userDetail", "flat", "flat.address"})
	Page<Review> findByFlatIdAndStatus(Long flatId, ReviewStatus status, Pageable pageable);
	
	@EntityGraph(attributePaths = {"user", "user.userDetail", "flat", "flat.address"})
	Page<Review> findByUserId(Long userId, Pageable pageable);
	
	@EntityGraph(attributePaths = {"user", "user.userDetail", "flat", "flat.address"})
	Page<Review> findByStatus(ReviewStatus status, Pageable pageable);
	
	/**
	 * Batch aggregates for a set of flat IDs.
	 * Returns rows of [flatId (Long), averageRating (Double), reviewCount (Long)].
	 * Only APPROVED reviews contribute.
	 */
	@Query("""
		SELECT r.flat.id, AVG(r.rating), COUNT(r)
		FROM Review r
		WHERE r.flat.id IN :flatIds AND r.status = 'APPROVED'
		GROUP BY r.flat.id
		""")
	List<Object[]> findRatingAggregates(@Param("flatIds") List<Long> flatIds);
	/*
	 Note: r.status = 'APPROVED' is a literal string in JPQL. If Hibernate complains, change to r.status = com.rentrix
	 .rentrixserver.entity.constants.ReviewStatus.APPROVED (FQN enum literal) or pass it as a parameter.
	 */
}