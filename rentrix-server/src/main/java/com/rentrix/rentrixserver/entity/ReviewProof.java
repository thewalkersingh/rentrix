package com.rentrix.rentrixserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "review_proofs", indexes = {@Index(name = "idx_review_proofs_review", columnList = "review_id"),
	@Index(name = "idx_review_proofs_order", columnList = "review_id, display_order")})
public class ReviewProof extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "review_id", nullable = false)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Review review;
	
	/** S3 key in the private bucket, e.g. "reviews/123/abc-uuid.pdf" */
	@Column(name = "storage_key", nullable = false, length = 500)
	private String storageKey;
	
	/** Original filename for admin reference, e.g. "rent-agreement-oct-2025.pdf" */
	@Column(name = "original_filename", length = 255)
	private String originalFilename;
	
	@Column(name = "content_type", nullable = false, length = 100)
	private String contentType;
	
	@Column(name = "size_bytes", nullable = false)
	private Long sizeBytes;
	
	@Column(name = "display_order", nullable = false)
	private Integer displayOrder = 0;
	
	/** Set to true when the parent review is approved */
	@Column(name = "verified", nullable = false)
	private Boolean verified = false;
	
	@Column(name = "deleted", nullable = false)
	private Boolean deleted = false;
	
}