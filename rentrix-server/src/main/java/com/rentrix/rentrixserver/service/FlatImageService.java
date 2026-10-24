package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.dto.request.CreateImageConfirmRequest;
import com.rentrix.rentrixserver.dto.response.FlatImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FlatImageService {
	
	List<FlatImageDto> listImages(Long flatId);
	
	FlatImageDto uploadImage(Long flatId, MultipartFile file, Long requesterId, boolean isAdmin);
	
	void deleteImage(Long flatId, Long imageId, Long requesterId, boolean isAdmin);
	
	FlatImageDto setPrimary(Long flatId, Long imageId, Long requesterId, boolean isAdmin);
	
	void assertCanManageFlat(Long flatId, Long requesterId, boolean isAdmin);
	
	FlatImageDto confirmImageUpload(Long flatId, CreateImageConfirmRequest req, Long requesterId, boolean isAdmin);
	
}