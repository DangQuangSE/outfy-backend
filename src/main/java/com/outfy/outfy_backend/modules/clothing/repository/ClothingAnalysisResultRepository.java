package com.outfy.outfy_backend.modules.clothing.repository;

import com.outfy.outfy_backend.modules.clothing.entity.ClothingAnalysisResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClothingAnalysisResultRepository extends JpaRepository<ClothingAnalysisResultEntity, Long> {
    Optional<ClothingAnalysisResultEntity> findByClothingItemId(Long clothingItemId);
}

