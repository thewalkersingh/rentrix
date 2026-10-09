package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.projection.RatingAggregate;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import com.rentrix.rentrixserver.dto.response.PageResponse;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.mapper.FlatMapper;
import com.rentrix.rentrixserver.repository.FlatRepository;
import com.rentrix.rentrixserver.repository.UserRepository;
import com.rentrix.rentrixserver.repository.specification.FlatSpecification;
import com.rentrix.rentrixserver.service.FlatService;
import com.rentrix.rentrixserver.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlatServiceImpl implements FlatService {
	
	private final FlatRepository flatRepository;
	private final UserRepository userRepository;
	private final ReviewService reviewService;
	
	@Override
	@Transactional(readOnly = true)
	public PageResponse<FlatSummaryResponse> listFlats(FlatFilterRequest filter, Pageable pageable) {
		log.info("Listing flats with filters: {}", filter);
		
		Page<Flat> page = flatRepository.findAll(
			FlatSpecification.visibleAndFiltered(filter), pageable);
		
		// Batch fetch ratings for the current page
		return flatSummaryPageResponse(page);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageResponse<FlatSummaryResponse> listMyFlats(Long userId, Pageable pageable) {
		log.info("Listing flats for user id={}", userId);
		
		Page<Flat> page = flatRepository.findByCreatedById(userId, pageable);
		return flatSummaryPageResponse(page);
	}
	
	@Override
	@Transactional(readOnly = true)
	public FlatResponse getFlatById(Long id) {
		Flat flat = flatRepository.findById(id)
										  .orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		
		RatingAggregate aggregate = reviewService
												 .getRatingAggregates(List.of(id))
												 .get(id);
		
		return FlatMapper.toResponse(flat, aggregate);
	}
	
	@Override
	@Transactional
	public FlatResponse createFlat(CreateFlatRequest request, Long creatorId, boolean isVerifiedCreator) {
		User creator = userRepository.findById(creatorId)
											  .orElseThrow(() -> ApiException.notFound("User not found"));
		
		Flat flat = FlatMapper.toEntity(request);
		flat.setCreatedBy(creator);
		flat.setVerified(isVerifiedCreator);
		flat.setVisible(isVerifiedCreator);
		
		if (isVerifiedCreator) {
			flat.setOwner(creator);
		}
		
		Flat saved = flatRepository.save(flat);
		log.info("Flat created: id={}, creator={}, verified={}",
			saved.getId(), creatorId, saved.getVerified());
		return FlatMapper.toResponse(saved);
	}
	
	@Override
	@Transactional
	public FlatResponse updateFlat(Long id, UpdateFlatRequest request, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(id)
										  .orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		
		boolean isOwner = flat.getOwner() != null && flat.getOwner().getId().equals(requesterId);
		boolean isCreator = flat.getCreatedBy() != null && flat.getCreatedBy().getId().equals(requesterId);
		
		if (!isAdmin && !isOwner && !isCreator) {
			throw ApiException.forbidden("You can only update flats you own or created");
		}
		
		FlatMapper.copyTo(flat, request);
		Flat saved = flatRepository.save(flat);
		log.info("Flat updated: id={} by user={}", id, requesterId);
		return FlatMapper.toResponse(saved);
	}
	
	@Override
	@Transactional
	public void deleteFlat(Long id, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(id)
										  .orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		
		boolean isOwner = flat.getOwner() != null && flat.getOwner().getId().equals(requesterId);
		boolean isCreator = flat.getCreatedBy() != null && flat.getCreatedBy().getId().equals(requesterId);
		
		if (!isAdmin && !isOwner && !isCreator) {
			throw ApiException.forbidden("You can only delete flats you own or created");
		}
		
		flatRepository.delete(flat);
		log.info("Flat soft-deleted: id={} by user={}", id, requesterId);
	}
	
	@Override
	@Transactional
	public FlatResponse setVerified(Long id, boolean verified) {
		Flat flat = flatRepository.findById(id)
										  .orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		flat.setVerified(verified);
		return FlatMapper.toResponse(flatRepository.save(flat));
	}
	
	@Override
	@Transactional
	public FlatResponse setVisible(Long id, boolean visible) {
		Flat flat = flatRepository.findById(id)
										  .orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		flat.setVisible(visible);
		return FlatMapper.toResponse(flatRepository.save(flat));
	}
	
	// Helper method to convert a Page<Flat> to PageResponse<T> with ratings
	private PageResponse<FlatSummaryResponse> flatSummaryPageResponse(Page<Flat> page) {
		List<Long> flatIds = page.getContent().stream().map(Flat::getId).toList();
		Map<Long, RatingAggregate> ratings = reviewService.getRatingAggregates(flatIds);
		
		return PageResponse.of(
			page.getContent().stream()
				 .map(flat -> FlatMapper.toSummary(flat, ratings.get(flat.getId())))
				 .toList(),
			page
		);
	}
	
}
/*
NOTES:
- @SQLRestriction hides deleted rows from all queries — including findById;
- @SQLDelete on the entity makes delete() perform UPDATE flats SET deleted = true
 */