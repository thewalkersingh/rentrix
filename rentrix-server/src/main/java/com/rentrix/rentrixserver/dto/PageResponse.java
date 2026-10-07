package com.rentrix.rentrixserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
	
	@JsonProperty("content")
	private List<T> content;
	
	@JsonProperty("totalElements")
	private long totalElements;
	
	@JsonProperty("totalPages")
	private int totalPages;
	
	@JsonProperty("number")
	private int number;
	
	@JsonProperty("size")
	private int size;
	
	/**
	 * Build a PageResponse from a Spring Data Page, applying a mapper
	 * to each element.
	 */
	public static <E, D> PageResponse<D> from(Page<E> page, Function<E, D> mapper) {
		return PageResponse.<D>builder()
								 .content(page.getContent().stream().map(mapper).toList())
								 .totalElements(page.getTotalElements())
								 .totalPages(page.getTotalPages())
								 .number(page.getNumber())
								 .size(page.getSize())
								 .build();
	}
	
	/**
	 * Build a PageResponse with an already-mapped list of content.
	 */
	public static <T> PageResponse<T> of(List<T> content, Page<?> page) {
		return PageResponse.<T>builder()
								 .content(content)
								 .totalElements(page.getTotalElements())
								 .totalPages(page.getTotalPages())
								 .number(page.getNumber())
								 .size(page.getSize())
								 .build();
	}
	
}

/*
Two factory methods:
1. from(page, mapper) — when you want to map as you build
2. of(content, page) — when you've already built the list (e.g., after batch-enriching with ratings)
 */