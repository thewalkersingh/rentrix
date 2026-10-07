package com.rentrix.rentrixserver.dto.response;

import com.rentrix.rentrixserver.dto.ReviewDto;
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
public class ReviewPageResponse {
	
	private List<ReviewDto> content;
	private long totalElements;
	private int totalPages;
	private int number;
	private int size;
	
	public static ReviewPageResponse from(Page<ReviewDto> page) {
		return ReviewPageResponse.builder()
										 .content(page.getContent())
										 .totalElements(page.getTotalElements())
										 .totalPages(page.getTotalPages())
										 .number(page.getNumber())
										 .size(page.getSize())
										 .build();
	}
	
}