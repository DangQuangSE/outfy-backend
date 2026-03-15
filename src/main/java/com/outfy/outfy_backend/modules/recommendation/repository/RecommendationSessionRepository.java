package com.outfy.outfy_backend.modules.recommendation.repository;

import com.outfy.outfy_backend.modules.recommendation.entity.RecommendationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationSessionRepository extends JpaRepository<RecommendationSession, Long> {
    List<RecommendationSession> findByUserId(Long userId);
}

