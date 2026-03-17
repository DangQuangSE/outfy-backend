package com.outfy.outfy_backend.modules.tryon.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.TryOnGateway;
import com.outfy.outfy_backend.modules.tryon.dto.request.CreateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.TryOnFromWardrobeRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.UpdateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResultResponse;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnSessionResponse;
import com.outfy.outfy_backend.modules.tryon.entity.TryOnResultEntity;
import com.outfy.outfy_backend.modules.tryon.entity.TryOnSession;
import com.outfy.outfy_backend.modules.tryon.mapper.TryOnMapper;
import com.outfy.outfy_backend.modules.tryon.repository.TryOnResultRepository;
import com.outfy.outfy_backend.modules.tryon.repository.TryOnSessionRepository;
import com.outfy.outfy_backend.modules.wardrobe.entity.WardrobeItem;
import com.outfy.outfy_backend.modules.wardrobe.repository.WardrobeItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TryOnService {

    private static final Logger logger = LoggerFactory.getLogger(TryOnService.class);

    private final TryOnSessionRepository tryOnSessionRepository;
    private final TryOnResultRepository tryOnResultRepository;
    private final WardrobeItemRepository wardrobeItemRepository;
    private final TryOnGateway tryOnGateway;
    private final ObjectMapper objectMapper;
    private final TryOnMapper tryOnMapper;

    public TryOnService(
            TryOnSessionRepository tryOnSessionRepository,
            TryOnResultRepository tryOnResultRepository,
            WardrobeItemRepository wardrobeItemRepository,
            TryOnGateway tryOnGateway,
            ObjectMapper objectMapper,
            TryOnMapper tryOnMapper) {
        this.tryOnSessionRepository = tryOnSessionRepository;
        this.tryOnResultRepository = tryOnResultRepository;
        this.wardrobeItemRepository = wardrobeItemRepository;
        this.tryOnGateway = tryOnGateway;
        this.objectMapper = objectMapper;
        this.tryOnMapper = tryOnMapper;
    }

    @Transactional
    public TryOnSessionResponse createTryOnSession(CreateTryOnSessionRequest request) {
        logger.info("Creating try-on session for user: {}, bodyProfile: {}, clothing: {}",
                request.getUserId(), request.getBodyProfileId(), request.getClothingItemIds());

        // Use mapper to convert request to entity
        TryOnSession session = tryOnMapper.toEntity(request);
        session.setStatus("PENDING");

        // Set avatar ID from body profile if available
        if (request.getBodyProfileId() != null) {
            session.setAvatarId("avatar_" + request.getBodyProfileId());
        }

        // Store clothing item IDs as JSON
        try {
            String clothingItemIdsJson = objectMapper.writeValueAsString(request.getClothingItemIds());
            session.setClothingItemIdsJson(clothingItemIdsJson);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing clothing item IDs", e);
        }

        TryOnSession saved = tryOnSessionRepository.save(session);
        logger.info("Created try-on session with id: {}", saved.getId());

        // Build response with clothing item IDs
        TryOnSessionResponse response = tryOnMapper.toResponse(saved);
        response.setClothingItemIds(request.getClothingItemIds());

        return response;
    }

    @Transactional
    public TryOnSessionResponse createTryOnFromWardrobe(TryOnFromWardrobeRequest request) {
        logger.info("Creating try-on session from wardrobe for user: {}, wardrobeItem: {}, avatar: {}",
                request.getUserId(), request.getWardrobeItemId(), request.getAvatarId());

        // Get wardrobe item to find clothing item ID
        WardrobeItem wardrobeItem = wardrobeItemRepository.findById(request.getWardrobeItemId())
                .orElseThrow(() -> new ResourceNotFoundException("WardrobeItem", "id", request.getWardrobeItemId()));

        // Verify ownership
        if (!wardrobeItem.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("Wardrobe item does not belong to user");
        }

        // Create session
        TryOnSession session = new TryOnSession();
        session.setUserId(request.getUserId());
        session.setWardrobeItemId(request.getWardrobeItemId());
        session.setClothingItemId(wardrobeItem.getClothingItemId());
        session.setAvatarId(request.getAvatarId());
        session.setRequestedSize(request.getSize());
        session.setFitType(request.getFitType());
        session.setGarmentCategory(wardrobeItem.getCategory());
        session.setGarmentColor(wardrobeItem.getColor());
        session.setStatus("PENDING");

        TryOnSession saved = tryOnSessionRepository.save(session);
        logger.info("Created try-on session from wardrobe with id: {}", saved.getId());

        return tryOnMapper.toResponse(saved);
    }

    public TryOnSessionResponse getTryOnSessionById(Long id) {
        TryOnSession session = tryOnSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnSession", "id", id));
        return tryOnMapper.toResponse(session);
    }

    public List<TryOnSessionResponse> getTryOnSessionsByUserId(Long userId) {
        return tryOnSessionRepository.findByUserId(userId).stream()
                .map(tryOnMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TryOnResult generateTryOnResult(Long sessionId) {
        logger.info("Generating try-on result for session id: {}", sessionId);

        TryOnSession session = tryOnSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnSession", "id", sessionId));

        // Update session status to PROCESSING
        session.setStatus("PROCESSING");
        tryOnSessionRepository.save(session);

        // Get avatar ID (use bodyProfileId as fallback or generate one)
        String avatarId = session.getAvatarId();
        if (avatarId == null && session.getBodyProfileId() != null) {
            avatarId = "avatar_" + session.getBodyProfileId();
        } else if (avatarId == null) {
            avatarId = "regular"; // default avatar
        }

        // Call gateway to generate try-on result with multiple clothing items
        List<Long> clothingItemIds = new ArrayList<>();

        // Try to get from JSON first, fallback to single item
        if (session.getClothingItemIdsJson() != null && !session.getClothingItemIdsJson().isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                List<Long> ids = objectMapper.readValue(session.getClothingItemIdsJson(), List.class);
                clothingItemIds = ids.stream()
                        .map(Number::longValue)
                        .collect(Collectors.toList());
            } catch (JsonProcessingException e) {
                logger.error("Error deserializing clothing item IDs", e);
                // Fallback to single item
                if (session.getClothingItemId() != null) {
                    clothingItemIds.add(session.getClothingItemId());
                }
            }
        } else if (session.getClothingItemId() != null) {
            // Legacy support - single item
            clothingItemIds.add(session.getClothingItemId());
        }

        TryOnResult result = tryOnGateway.generate(avatarId, clothingItemIds,
                session.getRequestedSize(), session.getFitType());

        // Save result
        TryOnResultEntity entity = new TryOnResultEntity();
        entity.setTryonSessionId(sessionId);
        entity.setPreviewUrl(result.getPreviewUrl());
        entity.setFitScore(result.getFitScore());
        entity.setResultNote(result.getNote());
        entity.setAvatarId(avatarId);

        // Get garment info from applied params
        Map<String, Object> appliedParams = result.getAppliedParams();
        if (appliedParams != null) {
            entity.setGarmentCategory((String) appliedParams.get("primaryCategory"));
            entity.setGarmentColor((String) appliedParams.get("garmentColors"));

            // Store garment categories as JSON
            try {
                String categoriesJson = objectMapper.writeValueAsString(appliedParams.get("garmentCategories"));
                entity.setGarmentCategoriesJson(categoriesJson);
            } catch (JsonProcessingException e) {
                logger.error("Error serializing garment categories", e);
            }
        }

        try {
            String appliedParamsJson = objectMapper.writeValueAsString(result.getAppliedParams());
            entity.setAppliedParamsJson(appliedParamsJson);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing applied params", e);
        }

        tryOnResultRepository.save(entity);

        // Update session status to COMPLETED
        session.setStatus("COMPLETED");
        tryOnSessionRepository.save(session);

        logger.info("Generated try-on result with fit score: {}", result.getFitScore());

        return result;
    }

    public TryOnResult getTryOnResult(Long sessionId) {
        TryOnResultEntity entity = tryOnResultRepository.findByTryonSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnResult", "sessionId", sessionId));

        TryOnResult result = new TryOnResult();
        result.setPreviewUrl(entity.getPreviewUrl());
        result.setFitScore(entity.getFitScore());
        result.setNote(entity.getResultNote());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> appliedParams = objectMapper.readValue(
                    entity.getAppliedParamsJson(), Map.class);
            result.setAppliedParams(appliedParams);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing applied params", e);
        }

        return result;
    }

    public TryOnResultResponse getTryOnResultResponse(Long sessionId) {
        TryOnResultEntity entity = tryOnResultRepository.findByTryonSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnResult", "sessionId", sessionId));

        TryOnResultResponse response = new TryOnResultResponse();
        response.setId(entity.getId());
        response.setSessionId(entity.getTryonSessionId());
        response.setPreviewUrl(entity.getPreviewUrl());
        response.setFitScore(entity.getFitScore());
        response.setNote(entity.getResultNote());
        response.setAvatarId(entity.getAvatarId());
        response.setAvatarUrl(entity.getAvatarUrl());

        // Single item (legacy)
        response.setGarmentCategory(entity.getGarmentCategory());
        response.setGarmentColor(entity.getGarmentColor());

        // Multiple items - deserialize from JSON
        if (entity.getGarmentCategoriesJson() != null && !entity.getGarmentCategoriesJson().isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                List<String> categories = objectMapper.readValue(entity.getGarmentCategoriesJson(), List.class);
                response.setGarmentCategories(categories);
            } catch (JsonProcessingException e) {
                logger.error("Error deserializing garment categories", e);
            }
        }

        response.setIsFavorite(entity.getIsFavorite());
        response.setCreatedAt(entity.getCreatedAt());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> appliedParams = objectMapper.readValue(
                    entity.getAppliedParamsJson(), Map.class);
            response.setAppliedParams(appliedParams);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing applied params", e);
        }

        return response;
    }

    @Transactional
    public TryOnSessionResponse updateTryOnSession(Long id, UpdateTryOnSessionRequest request) {
        logger.info("Updating try-on session: {}", id);

        TryOnSession session = tryOnSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnSession", "id", id));

        // Update fields
        if (request.getSize() != null) {
            session.setRequestedSize(request.getSize());
        }
        if (request.getFitType() != null) {
            session.setFitType(request.getFitType());
        }
        if (request.getSleeveLength() != null) {
            session.setSleeveLength(request.getSleeveLength());
        }
        if (request.getAvatarId() != null) {
            session.setAvatarId(request.getAvatarId());
        }

        TryOnSession saved = tryOnSessionRepository.save(session);
        logger.info("Updated try-on session: {}", id);

        return tryOnMapper.toResponse(saved);
    }

    @Transactional
    public TryOnResult regenerateTryOnResult(Long sessionId) {
        logger.info("Regenerating try-on result for session: {}", sessionId);

        TryOnSession session = tryOnSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnSession", "id", sessionId));

        // Delete old result if exists
        tryOnResultRepository.findByTryonSessionId(sessionId)
                .ifPresent(tryOnResultRepository::delete);

        // Reset session status
        session.setStatus("PENDING");
        tryOnSessionRepository.save(session);

        // Generate new result
        return generateTryOnResult(sessionId);
    }

    @Transactional
    public void deleteTryOnSession(Long id) {
        logger.info("Deleting try-on session: {}", id);

        TryOnSession session = tryOnSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnSession", "id", id));

        // Delete associated results
        tryOnResultRepository.findByTryonSessionId(id)
                .ifPresent(tryOnResultRepository::delete);

        tryOnSessionRepository.delete(session);
        logger.info("Deleted try-on session: {}", id);
    }

    @Transactional
    public TryOnResultResponse toggleFavorite(Long sessionId) {
        logger.info("Toggling favorite for try-on result: {}", sessionId);

        TryOnResultEntity entity = tryOnResultRepository.findByTryonSessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("TryOnResult", "sessionId", sessionId));

        Boolean currentFavorite = entity.getIsFavorite();
        entity.setIsFavorite(currentFavorite == null || !currentFavorite);
        TryOnResultEntity saved = tryOnResultRepository.save(entity);

        logger.info("Favorite toggled to: {} for session: {}", saved.getIsFavorite(), sessionId);

        return getTryOnResultResponse(sessionId);
    }

    public List<TryOnResultResponse> getFavoriteTryOnResults(Long userId) {
        List<TryOnSession> sessions = tryOnSessionRepository.findByUserId(userId);

        return sessions.stream()
                .map(session -> tryOnResultRepository.findByTryonSessionId(session.getId()))
                .filter(result -> result.isPresent() && Boolean.TRUE.equals(result.get().getIsFavorite()))
                .map(result -> {
                    TryOnResultEntity entity = result.get();
                    TryOnResultResponse response = new TryOnResultResponse();
                    response.setId(entity.getId());
                    response.setSessionId(entity.getTryonSessionId());
                    response.setPreviewUrl(entity.getPreviewUrl());
                    response.setFitScore(entity.getFitScore());
                    response.setNote(entity.getResultNote());
                    response.setAvatarId(entity.getAvatarId());
                    response.setAvatarUrl(entity.getAvatarUrl());
                    response.setGarmentCategory(entity.getGarmentCategory());
                    response.setGarmentColor(entity.getGarmentColor());

                    // Multiple items - deserialize from JSON
                    if (entity.getGarmentCategoriesJson() != null && !entity.getGarmentCategoriesJson().isEmpty()) {
                        try {
                            @SuppressWarnings("unchecked")
                            List<String> categories = objectMapper.readValue(entity.getGarmentCategoriesJson(), List.class);
                            response.setGarmentCategories(categories);
                        } catch (JsonProcessingException e) {
                            logger.error("Error deserializing garment categories", e);
                        }
                    }

                    response.setIsFavorite(entity.getIsFavorite());
                    response.setCreatedAt(entity.getCreatedAt());

                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> appliedParams = objectMapper.readValue(
                                entity.getAppliedParamsJson(), Map.class);
                        response.setAppliedParams(appliedParams);
                    } catch (JsonProcessingException e) {
                        logger.error("Error deserializing applied params", e);
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }
}

