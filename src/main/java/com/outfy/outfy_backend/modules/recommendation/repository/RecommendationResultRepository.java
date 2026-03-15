package com.outfy.outfy_backend.modules.recommendation.repository;

import com.outfy.outfy_backend.modules.recommendation.entity.RecommendationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationResultRepository extends JpaRepository<RecommendationResultEntity, Long> {
    List<RecommendationResultEntity> findByRecommendationSessionId(Long recommendationSessionId);
    Optional<RecommendationResultEntity> findTopByRecommendationSessionIdOrderByRankAsc(Long recommendationSessionId);
}

