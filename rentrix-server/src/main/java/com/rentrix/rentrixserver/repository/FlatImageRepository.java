package com.rentrix.rentrixserver.repository;

import com.rentrix.rentrixserver.entity.FlatImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlatImageRepository extends JpaRepository<FlatImage, Long> {
	
	List<FlatImage> findByFlatIdAndDeletedFalseOrderByDisplayOrderAsc(Long flatId);
	
	Optional<FlatImage> findByIdAndDeletedFalse(Long id);
	
	List<FlatImage> findByFlatIdInAndDeletedFalseOrderByFlatIdAscDisplayOrderAsc(List<Long> flatIds);
	
	Optional<FlatImage> findByFlatIdAndIsPrimaryTrueAndDeletedFalse(Long flatId);
	
	long countByFlatIdAndDeletedFalse(Long flatId);
	
}