package com.rentrix.rentrixserver.entity;

import com.rentrix.rentrixserver.entity.constants.PropertyType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
	name = "flats",
	indexes = {
		@Index(name = "idx_flats_city", columnList = "city"),
		@Index(name = "idx_flats_state", columnList = "state"),
		@Index(name = "idx_flats_rent", columnList = "rent"),
		@Index(name = "idx_flats_available", columnList = "available"),
		@Index(name = "idx_flats_owner", columnList = "owner_id"),
		@Index(name = "idx_flats_created_by", columnList = "created_by"),     // NEW
		@Index(name = "idx_flats_property", columnList = "property_type"),
		@Index(name = "idx_flats_visible", columnList = "visible"),        // NEW
		@Index(name = "idx_flats_deleted", columnList = "deleted")
	}
)
@SQLDelete(sql = "UPDATE flats SET deleted = TRUE WHERE id = ?")
@SQLRestriction("deleted = false")
@Data
public class Flat extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal rent;
	
	@Column(nullable = false)
	private Integer numberOfRooms;
	
	@Column(precision = 10, scale = 2)
	private BigDecimal area;
	
	private Integer floorNumber;
	private Integer totalFloors;
	private Boolean furnished;
	private Integer bathrooms;
	private Boolean parking;
	
	private LocalDate availableFrom;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private PropertyType propertyType;
	
	@Lob
	private String description;
	
	@Column(name = "available", nullable = false)
	private Boolean available = true;
	
	@Embedded
	private Address address;
	
	/** Set only when a LANDLORD or ADMIN creates/claims this flat. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	private User owner;
	
	/** Who originally created this flat record. Any authenticated user. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	private User createdBy;
	
	/**
	 * True when a LANDLORD/ADMIN created this flat, or an admin verified it.
	 * False for tenant-submitted flats until someone verifies the address.
	 */
	@Column(nullable = false)
	private Boolean verified = false;
	
	/**
	 * True when the flat should appear in public listings.
	 * Set true at creation for LANDLORD/ADMIN, or on first approved review
	 * for tenant-created flats.
	 */
	@Column(nullable = false)
	private Boolean visible = false;
	
	@Column(nullable = false)
	private Boolean deleted = false;
	
}