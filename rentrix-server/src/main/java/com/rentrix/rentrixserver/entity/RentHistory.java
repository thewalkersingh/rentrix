package com.rentrix.rentrixserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "rent_history")
public class RentHistory extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flat_id", nullable = false)
	private Flat flat;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "landlord_id")
	private User landlord;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id")
	private User tenant;
	
	private LocalDate rentStartDate;
	private LocalDate rentEndDate;
	
}
/*
One Strong Recommendation
In RentHistory:
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "landlord_id")
private User landlord;
Since every flat already has an owner: private User owner;
inside Flat, landlord becomes somewhat redundant.
For v0.1.2 keep it, but later you could simply derive landlord from:
rentHistory.getFlat().getOwner();
 */