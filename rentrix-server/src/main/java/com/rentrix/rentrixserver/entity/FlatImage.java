package com.rentrix.rentrixserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(
	name = "flat_images",
	indexes = {
		@Index(name = "idx_flat_images_flat", columnList = "flat_id"),
		@Index(name = "idx_flat_images_order", columnList = "flat_id, display_order")
	}
)
public class FlatImage extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "flat_id", nullable = false)
	@EqualsAndHashCode.Exclude
	private Flat flat;
	
	/** S3-compatible storage key, e.g. "flats/123/abc-uuid.jpg" */
	@Column(name = "storage_key", nullable = false, length = 500)
	private String storageKey;
	
	/** Publicly-accessible URL for the frontend to render */
	@Column(name = "public_url", nullable = false, length = 1000)
	private String publicUrl;
	
	/** MIME type, e.g. image/jpeg, image/png, image/webp */
	@Column(name = "content_type", nullable = false, length = 100)
	private String contentType;
	
	/** File size in bytes */
	@Column(name = "size_bytes", nullable = false)
	private Long sizeBytes;
	
	/** Original filename (for user reference, never served) */
	@Column(name = "original_filename", length = 255)
	private String originalFilename;
	
	/** Display order in gallery, 0-indexed */
	@Column(name = "display_order", nullable = false)
	private Integer displayOrder = 0;
	
	/** Primary image shown on cards */
	@Column(name = "is_primary", nullable = false)
	private Boolean isPrimary = false;
	
	@Column(name = "deleted", nullable = false)
	private Boolean deleted = false;
	
}