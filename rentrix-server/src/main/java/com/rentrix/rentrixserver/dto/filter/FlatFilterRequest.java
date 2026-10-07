package com.rentrix.rentrixserver.dto.filter;

import com.rentrix.rentrixserver.entity.constants.PropertyType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FlatFilterRequest {
	
	private String city;
	private String state;
	
	private BigDecimal minRent;
	private BigDecimal maxRent;
	
	private Integer minRooms;
	private Integer maxRooms;
	
	private Boolean furnished;
	private Boolean parking;
	
	private PropertyType propertyType;
	
	private Boolean available;
	
	// "available from" — flats available by this date
	private LocalDate availableFrom;
	
	// Free-text search across address + description
	private String q;
	
	// v0.1.2 — review-based filters
	/** Minimum average rating (1–10). Null = no rating filter. */
	private Integer minRating;
	
	/** When true, only flats with at least one APPROVED review. */
	private Boolean hasReviews;
	
}