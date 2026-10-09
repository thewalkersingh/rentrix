package com.rentrix.rentrixserver.entity;

import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

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
	
	// ── v0.1.3 — Proof of living ────────────────────────────────────────
	
	@Column(name = "proof_storage_key", length = 500)
	private String proofStorageKey;
	
	@Column(name = "proof_content_type", length = 100)
	private String proofContentType;
	
	@Column(name = "proof_uploaded_at")
	private LocalDateTime proofUploadedAt;
	
	@Column(name = "proof_verified", nullable = false)
	private Boolean proofVerified = false;
	
}