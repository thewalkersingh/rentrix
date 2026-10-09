package com.rentrix.rentrixserver.dto.response;

import com.rentrix.rentrixserver.dto.common.AddressDto;
import com.rentrix.rentrixserver.entity.constants.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatResponse {
	
	private Long id;
	
	private BigDecimal rent;
	private Integer numberOfRooms;
	private BigDecimal area;
	private Integer floorNumber;
	private Integer totalFloors;
	private Boolean furnished;
	private Integer bathrooms;
	private Boolean parking;
	private LocalDate availableFrom;
	private PropertyType propertyType;
	private String description;
	private Boolean available;
	
	private AddressDto address;
	
	private Long ownerId;
	private String ownerName;
	
	private Long createdById;
	private String createdByName;
	
	private Boolean verified;
	private Boolean visible;
	
	private Double averageRating;
	private Long reviewCount;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private List<FlatImageDto> images;
	private String primaryImageUrl;
	
}