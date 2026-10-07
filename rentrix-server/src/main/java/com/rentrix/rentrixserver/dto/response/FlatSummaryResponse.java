package com.rentrix.rentrixserver.dto.response;

import com.rentrix.rentrixserver.entity.constants.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatSummaryResponse {
	
	private Long id;
	private BigDecimal rent;
	private Integer numberOfRooms;
	private BigDecimal area;
	private Boolean furnished;
	private Integer bathrooms;
	private Boolean parking;
	private LocalDate availableFrom;
	private PropertyType propertyType;
	private Boolean available;
	
	// Just city + state for the card
	private String city;
	private String state;
	private String addressLine;
	
	// Rating summary
	private Double averageRating;
	private Long reviewCount;
	
	private Boolean verified;
	private Boolean visible;
	
}