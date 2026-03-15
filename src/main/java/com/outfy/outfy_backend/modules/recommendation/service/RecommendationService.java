package com.outfy.outfy_backend.modules.recommendation.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.RecommendationGateway;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
import com.outfy.outfy_backend.modules.clothing.repository.ClothingItemRepository;
import com.outfy.outfy_backend.modules.recommendation.dto.request.CreateRecommendationRequest;
import com.outfy.outfy_backend.modules.recommendation.dto.response.RecommendationItemResponse;
import com.outfy.outfy_backend.modules.recommendation.dto.response.RecommendationSessionResponse;
import com.outfy.outfy_backend.modules.recommendation.entity.RecommendationResultEntity;
import com.outfy.outfy_backend.modules.recommendation.entity.RecommendationSession;
import com.outfy.outfy_backend.modules.recommendation.repository.RecommendationResultRepository;
import com.outfy.outfy_backend.modules.recommendation.repository.RecommendationSessionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final RecommendationSessionRepository recommendationSessionRepository;
    private final RecommendationResultRepository recommendationResultRepository;
    private final RecommendationGateway recommendationGateway;
    private final ClothingItemRepository clothingItemRepository;
    private final ObjectMapper objectMapper;

    public RecommendationService(
            RecommendationSessionRepository recommendationSessionRepository,
            RecommendationResultRepository recommendationResultRepository,
            RecommendationGateway recommendationGateway,
            ClothingItemRepository clothingItemRepository,
            ObjectMapper objectMapper) {
        this.recommendationSessionRepository = recommendationSessionRepository;
        this.recommendationResultRepository = recommendationResultRepository;
        this.recommendationGateway = recommendationGateway;
        this.clothingItemRepository = clothingItemRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RecommendationSessionResponse createRecommendation(CreateRecommendationRequest request) {
        logger.info("Creating recommendation session for user: {}", request.getUserId());

        RecommendationSession session = new RecommendationSession();
        session.setUserId(request.getUserId());
        session.setBodyProfileId(request.getBodyProfileId());
        session.setOccasion(request.getOccasion());
        session.setStatus("PENDING");

        if (request.getPreferences() != null) {
            try {
                session.setPreferenceJson(objectMapper.writeValueAsString(request.getPreferences()));
            } catch (JsonProcessingException e) {
                logger.error("Error serializing preferences", e);
            }
        }

        RecommendationSession saved = recommendationSessionRepository.save(session);
        logger.info("Created recommendation session with id: {}", saved.getId());

        return toResponse(saved);
    }

    public RecommendationSessionResponse getRecommendationById(Long id) {
        RecommendationSession session = recommendationSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RecommendationSession", "id", id));
        return toResponse(session);
    }

    public List<RecommendationSessionResponse> getRecommendationsByUserId(Long userId) {
        return recommendationSessionRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecommendationSessionResponse generateRecommendations(Long sessionId) {
        logger.info("Generating recommendations for session id: {}", sessionId);

        RecommendationSession session = recommendationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("RecommendationSession", "id", sessionId));

        // Update session status to PROCESSING
        session.setStatus("PROCESSING");
        recommendationSessionRepository.save(session);

        // Get user's clothing items
        List<ClothingItem> userClothing = clothingItemRepository.findByUserId(session.getUserId());

        // Call gateway to get recommendations
        List<Map<String, Object>> recommendationResults = recommendationGateway.recommend(
                session.getUserId(),
                session.getBodyProfileId(),
                session.getOccasion(),
                userClothing.stream().map(ClothingItem::getId).toList());

        // Save results
        for (int i = 0; i < recommendationResults.size(); i++) {
            Map<String, Object> result = recommendationResults.get(i);
            RecommendationResultEntity entity = new RecommendationResultEntity();
            entity.setRecommendationSessionId(sessionId);
            entity.setClothingItemId(((Number) result.get("clothingItemId")).longValue());
            entity.setMatchScore(((Number) result.get("matchScore")).doubleValue());
            entity.setReason((String) result.get("reason"));
            entity.setRank(i + 1);
            recommendationResultRepository.save(entity);
        }

        // Update session status to COMPLETED
        session.setStatus("COMPLETED");
        recommendationSessionRepository.save(session);

        logger.info("Generated {} recommendations", recommendationResults.size());

        return toResponseWithItems(session);
    }

    public RecommendationSessionResponse getRecommendationWithItems(Long sessionId) {
        RecommendationSession session = recommendationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("RecommendationSession", "id", sessionId));
        return toResponseWithItems(session);
    }

    private RecommendationSessionResponse toResponse(RecommendationSession session) {
        RecommendationSessionResponse response = new RecommendationSessionResponse();
        response.setId(session.getId());
        response.setUserId(session.getUserId());
        response.setBodyProfileId(session.getBodyProfileId());
        response.setOccasion(session.getOccasion());
        response.setStatus(session.getStatus());
        response.setCreatedAt(session.getCreatedAt());
        return response;
    }

    private RecommendationSessionResponse toResponseWithItems(RecommendationSession session) {
        RecommendationSessionResponse response = toResponse(session);

        List<RecommendationResultEntity> results = recommendationResultRepository
                .findByRecommendationSessionId(session.getId());

        List<RecommendationItemResponse> items = results.stream()
                .map(result -> {
                    RecommendationItemResponse item = new RecommendationItemResponse();
                    item.setClothingItemId(result.getClothingItemId());
                    item.setMatchScore(result.getMatchScore());
                    item.setReason(result.getReason());
                    item.setRank(result.getRank());

                    // Get image URL from clothing item
                    clothingItemRepository.findById(result.getClothingItemId())
                            .ifPresent(clothing -> item.setImageUrl(clothing.getImageUrl()));

                    return item;
                })
                .collect(Collectors.toList());

        response.setItems(items);
        return response;
    }
}

