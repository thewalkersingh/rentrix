package com.rentrix.rentrixserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatImageDto {
	
	private Long id;
	private String url;
	private Integer displayOrder;
	private Boolean isPrimary;
	
}