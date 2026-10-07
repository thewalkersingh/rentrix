package com.rentrix.rentrixserver.repository.specification;

import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.entity.Flat;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class FlatSpecification {
	
	private FlatSpecification() {}
	
	public static Specification<Flat> visibleAndFiltered(FlatFilterRequest filter) {
		return withFilters(filter)
					 .and((root, query, cb) -> cb.isTrue(root.get("visible")));
	}
	
	/**
	 * Builds a composable Specification from the filter request.
	 * Every non-null filter becomes an AND predicate.
	 */
	
	public static Specification<Flat> withFilters(FlatFilterRequest filter) {
		return (root, query, cb) -> {
			if (filter == null) return cb.conjunction();
			
			List<Predicate> predicates = new ArrayList<>();
			
			// Address city / state (embedded)
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
				predicates.add(cb.equal(root.get("available"), filter.getAvailable()));
			}
			
			// Property type
			if (filter.getPropertyType() != null) {
				predicates.add(cb.equal(root.get("propertyType"), filter.getPropertyType()));
			}
			
			// Available by date (availableFrom <= filter.availableFrom)
			if (filter.getAvailableFrom() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("availableFrom"), filter.getAvailableFrom()));
			}
			
			// Free-text search across addressLine + description
			if (filter.getQ() != null && !filter.getQ().isBlank()) {
				String q = "%" + filter.getQ().toLowerCase().trim() + "%";
				predicates.add(cb.or(cb.like(cb.lower(root.get("address").get("addressLine")), q),
					cb.like(cb.lower(root.get("description")), q)));
			}
			
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
	
}