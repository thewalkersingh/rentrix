package com.rentrix.rentrixserver.entity;

import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "reviews", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "flat_id"})})
public class Review extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flat_id", nullable = false)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Flat flat;
	
	@NotBlank
	private String title;
	
	@NotBlank
	@Lob
	private String content;
	
	@Min(1)
	@Max(10)
	private Integer rating;
	
	@Enumerated(EnumType.STRING)
	private ReviewStatus status = ReviewStatus.PENDING;
	
	private Boolean deleted = false;
	
	// v0.1.3 — proof of living
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<ReviewProof> proofs = new ArrayList<>();
	
}