package com.rentrix.rentrixserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlatPageResponse {
	
	private List<FlatSummaryResponse> content;
	private long totalElements;
	private int totalPages;
	private int number;
	private int size;
	
	public static FlatPageResponse from(Page<FlatSummaryResponse> page) {
		return FlatPageResponse.builder()
									  .content(page.getContent())
									  .totalElements(page.getTotalElements())
									  .totalPages(page.getTotalPages())
									  .number(page.getNumber())
									  .size(page.getSize())
									  .build();
	}
	
}