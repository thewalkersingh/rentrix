package com.rentrix.rentrixserver.repository.specification;

import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.Review;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class FlatSpecification {
	
	private FlatSpecification() {}
	
	public static Specification<Flat> visibleAndFiltered(FlatFilterRequest filter) {
		return withFilters(filter)
					 .and((root, query, cb) -> cb.equal(root.get("visible"), true));
	}
	
	public static Specification<Flat> withFilters(FlatFilterRequest filter) {
		return (root, query, cb) -> {
			if (filter == null) return cb.conjunction();
			
			List<Predicate> predicates = new ArrayList<>();
			
			// Address city / state
			if (filter.getCity() != null && !filter.getCity().isBlank()) {
				String city = "%" + filter.getCity().toLowerCase().trim() + "%";
				predicates.add(cb.like(cb.lower(root.get("address").get("city")), city));
			}
			if (filter.getState() != null && !filter.getState().isBlank()) {
				String state = "%" + filter.getState().toLowerCase().trim() + "%";
				predicates.add(cb.like(cb.lower(root.get("address").get("state")), state));
			}
			
			// Rent range
			if (filter.getMinRent() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("rent"), filter.getMinRent()));
			}
			if (filter.getMaxRent() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("rent"), filter.getMaxRent()));
			}
			
			// Rooms range
			if (filter.getMinRooms() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("numberOfRooms"), filter.getMinRooms()));
			}
			if (filter.getMaxRooms() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("numberOfRooms"), filter.getMaxRooms()));
			}
			
			// Booleans
			if (filter.getFurnished() != null) {
				predicates.add(cb.equal(root.get("furnished"), filter.getFurnished()));
			}
			if (filter.getParking() != null) {
				predicates.add(cb.equal(root.get("parking"), filter.getParking()));
			}
			if (filter.getAvailable() != null) {
				predicates.add(cb.equal(root.get("isAvailable"), filter.getAvailable()));
			}
			
			// Property type
			if (filter.getPropertyType() != null) {
				predicates.add(cb.equal(root.get("propertyType"), filter.getPropertyType()));
			}
			
			// Available by date
			if (filter.getAvailableFrom() != null) {
				predicates.add(cb.lessThanOrEqualTo(
					root.get("availableFrom"), filter.getAvailableFrom()));
			}
			
			// Free-text search
			if (filter.getQ() != null && !filter.getQ().isBlank()) {
				String q = "%" + filter.getQ().toLowerCase().trim() + "%";
				predicates.add(cb.or(
					cb.like(cb.lower(root.get("address").get("addressLine")), q),
					cb.like(cb.lower(root.get("description")), q)
				));
			}
			
			// ── Review-based filters ──────────────────────────────────────────
			
			// Min average rating — subquery on AVG(rating) for approved reviews
			if (filter.getMinRating() != null) {
				Subquery<Double> avgSub = query.subquery(Double.class);
				Root<Review> reviewRoot = avgSub.from(Review.class);
				avgSub.select(cb.avg(reviewRoot.get("rating")))
						.where(cb.and(
							cb.equal(reviewRoot.get("flat"), root),
							cb.equal(reviewRoot.get("status"), ReviewStatus.APPROVED)
						));
				predicates.add(cb.greaterThanOrEqualTo(avgSub, filter.getMinRating().doubleValue()));
			}
			
			// Has reviews — subquery on COUNT > 0
			if (Boolean.TRUE.equals(filter.getHasReviews())) {
				Subquery<Long> countSub = query.subquery(Long.class);
				Root<Review> reviewRoot = countSub.from(Review.class);
				countSub.select(cb.count(reviewRoot))
						  .where(cb.and(
							  cb.equal(reviewRoot.get("flat"), root),
							  cb.equal(reviewRoot.get("status"), ReviewStatus.APPROVED)
						  ));
				predicates.add(cb.greaterThan(countSub, 0L));
			}
			
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
	
}