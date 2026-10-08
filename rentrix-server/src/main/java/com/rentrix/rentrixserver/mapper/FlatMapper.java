package com.rentrix.rentrixserver.mapper;

import com.rentrix.rentrixserver.dto.common.AddressDto;
import com.rentrix.rentrixserver.dto.projection.RatingAggregate;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import com.rentrix.rentrixserver.entity.Address;
import com.rentrix.rentrixserver.entity.Flat;

public final class FlatMapper {
	
	private FlatMapper() {}
	
	// ── Entity → Response (detail) ─────────────────────────────────────────
	
	public static FlatResponse toResponse(Flat flat) {
		if (flat == null) return null;
		
		FlatResponse.FlatResponseBuilder b = FlatResponse.builder()
																		 .id(flat.getId())
																		 .rent(flat.getRent())
																		 .numberOfRooms(flat.getNumberOfRooms())
																		 .area(flat.getArea())
																		 .floorNumber(flat.getFloorNumber())
																		 .totalFloors(flat.getTotalFloors())
																		 .furnished(flat.getFurnished())
																		 .bathrooms(flat.getBathrooms())
																		 .parking(flat.getParking())
																		 .availableFrom(flat.getAvailableFrom())
																		 .propertyType(flat.getPropertyType())
																		 .description(flat.getDescription())
																		 .available(flat.getAvailable())
																		 .address(toAddressDto(flat.getAddress()))
																		 .verified(flat.getVerified())
																		 .visible(flat.getVisible())
																		 .createdAt(flat.getCreatedAt())
																		 .updatedAt(flat.getUpdatedAt());
		
		if (flat.getOwner() != null) {
			b.ownerId(flat.getOwner().getId())
			 .ownerName(flat.getOwner().getDisplayName());
		}
		if (flat.getCreatedBy() != null) {
			b.createdById(flat.getCreatedBy().getId())
			 .createdByName(flat.getCreatedBy().getDisplayName());
		}
		
		b.averageRating(null).reviewCount(null);
		return b.build();
	}
	
	public static FlatResponse toResponse(Flat flat, RatingAggregate aggregate) {
		FlatResponse response = toResponse(flat);
		if (response != null && aggregate != null) {
			response.setAverageRating(aggregate.averageRating());
			response.setReviewCount(aggregate.reviewCount());
		}
		return response;
	}
	
	// ── Entity → Summary (list view) ───────────────────────────────────────
	
	public static FlatSummaryResponse toSummary(Flat flat) {
		if (flat == null) return null;
		
		String city = flat.getAddress() != null ? flat.getAddress().getCity() : null;
		String state = flat.getAddress() != null ? flat.getAddress().getState() : null;
		String addressLine = flat.getAddress() != null ? flat.getAddress().getAddressLine() : null;
		
		return FlatSummaryResponse.builder()
										  .id(flat.getId())
										  .rent(flat.getRent())
										  .numberOfRooms(flat.getNumberOfRooms())
										  .area(flat.getArea())
										  .furnished(flat.getFurnished())
										  .bathrooms(flat.getBathrooms())
										  .parking(flat.getParking())
										  .availableFrom(flat.getAvailableFrom())
										  .propertyType(flat.getPropertyType())
										  .available(flat.getAvailable())
										  .addressLine(addressLine)
										  .city(city)
										  .state(state)
										  .verified(flat.getVerified())
										  .visible(flat.getVisible())
										  .averageRating(null)
										  .reviewCount(null)
										  .build();
	}
	
	public static FlatSummaryResponse toSummary(Flat flat, RatingAggregate aggregate) {
		FlatSummaryResponse response = toSummary(flat);
		if (response != null && aggregate != null) {
			response.setAverageRating(aggregate.averageRating());
			response.setReviewCount(aggregate.reviewCount());
		}
		return response;
	}
	
	// ── Request → Entity ───────────────────────────────────────────────────
	
	public static Flat toEntity(CreateFlatRequest req) {
		if (req == null) return null;
		Flat flat = new Flat();
		copyTo(flat, req);
		return flat;
	}
	
	public static void copyTo(Flat flat, CreateFlatRequest req) {
		if (flat == null || req == null) return;
		flat.setRent(req.getRent());
		flat.setNumberOfRooms(req.getNumberOfRooms());
		flat.setArea(req.getArea());
		flat.setFloorNumber(req.getFloorNumber());
		flat.setTotalFloors(req.getTotalFloors());
		flat.setFurnished(req.getFurnished());
		flat.setBathrooms(req.getBathrooms());
		flat.setParking(req.getParking());
		flat.setAvailableFrom(req.getAvailableFrom());
		flat.setPropertyType(req.getPropertyType());
		flat.setDescription(req.getDescription());
		flat.setAvailable(req.getAvailable() != null ? req.getAvailable() : true);
		flat.setAddress(toAddressEntity(req.getAddress()));
	}
	
	public static void copyTo(Flat flat, UpdateFlatRequest req) {
		if (flat == null || req == null) return;
		if (req.getRent() != null) flat.setRent(req.getRent());
		if (req.getNumberOfRooms() != null) flat.setNumberOfRooms(req.getNumberOfRooms());
		if (req.getArea() != null) flat.setArea(req.getArea());
		if (req.getFloorNumber() != null) flat.setFloorNumber(req.getFloorNumber());
		if (req.getTotalFloors() != null) flat.setTotalFloors(req.getTotalFloors());
		if (req.getFurnished() != null) flat.setFurnished(req.getFurnished());
		if (req.getBathrooms() != null) flat.setBathrooms(req.getBathrooms());
		if (req.getParking() != null) flat.setParking(req.getParking());
		if (req.getAvailableFrom() != null) flat.setAvailableFrom(req.getAvailableFrom());
		if (req.getPropertyType() != null) flat.setPropertyType(req.getPropertyType());
		if (req.getDescription() != null) flat.setDescription(req.getDescription());
		if (req.getAvailable() != null) flat.setAvailable(req.getAvailable());
		if (req.getAddress() != null) {
			Address addr = flat.getAddress();
			if (addr == null) {
				addr = new Address();
				flat.setAddress(addr);
			}
			copyAddress(addr, req.getAddress());
		}
	}
	
	// ── Address helpers ────────────────────────────────────────────────────
	
	private static AddressDto toAddressDto(Address a) {
		if (a == null) return null;
		return AddressDto.builder()
							  .addressLine(a.getAddressLine())
							  .street(a.getStreet())
							  .city(a.getCity())
							  .state(a.getState())
							  .zipCode(a.getZipCode())
							  .build();
	}
	
	private static Address toAddressEntity(AddressDto dto) {
		if (dto == null) return null;
		Address a = new Address();
		a.setAddressLine(dto.getAddressLine());
		a.setStreet(dto.getStreet());
		a.setCity(dto.getCity());
		a.setState(dto.getState());
		a.setZipCode(dto.getZipCode());
		return a;
	}
	
	private static void copyAddress(Address target, AddressDto src) {
		if (src.getAddressLine() != null) target.setAddressLine(src.getAddressLine());
		if (src.getStreet() != null) target.setStreet(src.getStreet());
		if (src.getCity() != null) target.setCity(src.getCity());
		if (src.getState() != null) target.setState(src.getState());
		if (src.getZipCode() != null) target.setZipCode(src.getZipCode());
	}
	
}