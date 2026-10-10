package com.rentrix.rentrixserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewProofDto {
	
	private Long id;
	private String originalFilename;
	private String contentType;
	private Long sizeBytes;
	private Integer displayOrder;
	private Boolean verified;
	
}