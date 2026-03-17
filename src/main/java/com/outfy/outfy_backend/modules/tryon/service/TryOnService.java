package com.outfy.outfy_backend.modules.tryon.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.TryOnGateway;
import com.outfy.outfy_backend.modules.tryon.dto.request.QuickTryOnRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.CreateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.TryOnFromWardrobeRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.UpdateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.response.QuickTryOnResponse;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResultResponse;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnSessionResponse;
import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyProfile;
import com.outfy.outfy_backend.modules.bodyprofile.repository.BodyProfileRepository;
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

    // Base path for try-on models
    private static final String TRYON_MODEL_PATH = "/models/try-on";

    // Available models mapping
    private static final Map<String, String> TRYON_MODELS = Map.ofEntries(
            Map.entry("slim_female_cloth_crop_top_short_skirt", "body_slim_female_cloth_crop_top_short_skirt.glb"),
            Map.entry("slim_female_cloth_dress", "body_slim_female_cloth_dress.glb"),
            Map.entry("regular_female_cloth_female_tshirt_shorts", "body_regular_female_cloth_female_tshirt_shorts.glb"),
            Map.entry("regular_male_cloth_tshirt_pants", "body_regular_male_cloth_tshirt_pants.glb"),
            Map.entry("broad_male_cloth_hoodie_pants", "body_broad_male_cloth_hoodie_pants.glb"),
            Map.entry("broad_male_cloth_jacket_pants", "body_broad_male_cloth_jacket_pants.glb")
    );

    private final TryOnSessionRepository tryOnSessionRepository;
    private final TryOnResultRepository tryOnResultRepository;
    private final WardrobeItemRepository wardrobeItemRepository;
    private final BodyProfileRepository bodyProfileRepository;
    private final TryOnGateway tryOnGateway;
    private final ObjectMapper objectMapper;
    private final TryOnMapper tryOnMapper;

    public TryOnService(
            TryOnSessionRepository tryOnSessionRepository,
            TryOnResultRepository tryOnResultRepository,
            WardrobeItemRepository wardrobeItemRepository,
            BodyProfileRepository bodyProfileRepository,
            TryOnGateway tryOnGateway,
            ObjectMapper objectMapper,
            TryOnMapper tryOnMapper) {
        this.tryOnSessionRepository = tryOnSessionRepository;
        this.tryOnResultRepository = tryOnResultRepository;
        this.wardrobeItemRepository = wardrobeItemRepository;
        this.bodyProfileRepository = bodyProfileRepository;
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

    /**
     * Quick try-on: Map body type + clothing items to available 3D model
     * Returns URL to GLB file for 360 display
     */
    public QuickTryOnResponse quickTryOn(QuickTryOnRequest request) {
        logger.info("Quick try-on for user: {}, bodyProfile: {}, gender: {}, bodyType: {}, wardrobeItems: {}",
                request.getUserId(), request.getBodyProfileId(), request.getGender(),
                request.getBodyType(), request.getWardrobeItemIds());

        String gender;
        String bodyType;

        // 1. Determine body type - either from bodyProfileId OR from direct params
        if (request.getBodyProfileId() != null) {
            // Get from body profile
            BodyProfile bodyProfile = bodyProfileRepository.findById(request.getBodyProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("BodyProfile", "id", request.getBodyProfileId()));

            if (!bodyProfile.getUserId().equals(request.getUserId())) {
                throw new IllegalArgumentException("Body profile does not belong to user");
            }

            gender = bodyProfile.getGender();
            bodyType = determineBodyTypeFromProfile(bodyProfile);
        } else if (request.getGender() != null && request.getBodyType() != null) {
            // Use directly provided gender and body type
            gender = request.getGender();
            bodyType = request.getBodyType();
        } else {
            throw new IllegalArgumentException("Either bodyProfileId or gender+bodyType must be provided");
        }

        // 2. Get wardrobe items and their categories
        List<WardrobeItem> wardrobeItems = wardrobeItemRepository.findAllById(request.getWardrobeItemIds());
        if (wardrobeItems.isEmpty()) {
            throw new IllegalArgumentException("No wardrobe items found");
        }

        // Filter to only user's items
        List<WardrobeItem> userItems = wardrobeItems.stream()
                .filter(item -> item.getUserId() != null && item.getUserId().equals(request.getUserId()))
                .collect(Collectors.toList());

        if (userItems.isEmpty()) {
            throw new IllegalArgumentException("Wardrobe items do not belong to user");
        }

        List<String> categories = userItems.stream()
                .map(item -> item.getCategory() != null ? item.getCategory().toUpperCase() : "SHIRT")
                .collect(Collectors.toList());

        // 3. Map to available model - format: body_{bodyType}_{gender}_cloth_{category1}_{category2}
        String modelFileName = findMatchingModel(bodyType, gender, categories);

        // 4. Build response
        QuickTryOnResponse response = new QuickTryOnResponse();
        response.setModelUrl(TRYON_MODEL_PATH + "/" + modelFileName);
        response.setModelFileName(modelFileName);
        response.setBodyType(bodyType);
        response.setGender(gender);
        response.setClothingCategories(categories);
        response.setFitScore(calculateFitScore(bodyType, categories));
        response.setMessage("Try-on model loaded successfully");

        logger.info("Quick try-on result: bodyType={}, gender={}, model={}",
                bodyType, gender, modelFileName);

        return response;
    }

    /**
     * Determine body type from body profile measurements
     */
    private String determineBodyTypeFromProfile(BodyProfile profile) {
        double heightM = profile.getHeightCm() / 100.0;
        double bmi = profile.getWeightKg() / (heightM * heightM);
        double waistToHipRatio = profile.getWaistCm() / profile.getHipCm();

        String gender = profile.getGender();
        boolean isFemale = "female".equalsIgnoreCase(gender);

        if (isFemale) {
            if (bmi < 18.5) return "slim";
            else if (bmi < 24) {
                return waistToHipRatio > 0.75 ? "curvy" : "regular";
            } else if (bmi < 28) return "curvy";
            else return "curvy";
        } else {
            if (bmi < 18.5) return "slim";
            else if (bmi < 25) {
                return profile.getShoulderCm() > 45 ? "broad" : "regular";
            } else if (bmi < 30) return "broad";
            else return "broad";
        }
    }

    /**
     * Map clothing category to garment part for model matching
     */
    private String mapCategoryToGarmentPart(String category) {
        if (category == null) return "tshirt_pants";

        switch (category.toUpperCase()) {
            case "HOODIE":
                return "hoodie_pants";
            case "T-SHIRT":
            case "TSHIRT":
                return "tshirt_pants";
            case "FEMALE_TSHIRT":
                return "female_tshirt_shorts";
            case "SHIRT":
                return "tshirt_pants";
            case "JACKET":
                return "jacket_pants";
            case "PANTS":
                return "tshirt_pants";
            case "SHORTS":
                return "tshirt_pants";
            case "DRESS":
                return "dress";
            case "SKIRT":
                return "short_skirt";
            case "SHORT_SKIRT":
                return "short_skirt";
            case "CROP_TOP":
                return "crop_top_short_skirt";
            default:
                return "tshirt_pants";
        }
    }

    /**
     * Find matching model from available models
     * Format: body_{bodyType}_{gender}_cloth_{category1}_{category2}.glb
     */
    private String findMatchingModel(String bodyType, String gender, List<String> categories) {
        // Build model filename directly: body_{bodyType}_{gender}_cloth_{category1}_{category2}
        String normalizedGender = (gender != null) ? gender.toLowerCase() : "female";
        String normalizedBodyType = (bodyType != null) ? bodyType.toLowerCase() : "regular";

        // Build garment parts from categories
        String garmentParts = buildGarmentParts(categories);

        // Build the expected model key (format: bodyType_gender_cloth_garmentParts)
        String modelKey = normalizedBodyType + "_" + normalizedGender + "_cloth_" + garmentParts;

        logger.info("Looking for model with key: {}", modelKey);

        // Try exact match first
        if (TRYON_MODELS.containsKey(modelKey)) {
            return TRYON_MODELS.get(modelKey);
        }

        // Try partial match - find first model with matching gender + body type
        for (Map.Entry<String, String> entry : TRYON_MODELS.entrySet()) {
            String key = entry.getKey();
            if (key.contains(normalizedGender) && key.contains(normalizedBodyType)) {
                logger.info("Found partial match: {}", key);
                return entry.getValue();
            }
        }

        // Fallback: return first available model (should not happen with proper data)
        logger.warn("No matching model found, using fallback");
        return TRYON_MODELS.values().iterator().next();
    }

    /**
     * Build garment parts string from categories
     */
    private String buildGarmentParts(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return "tshirt_pants";
        }

        boolean hasTop = false;
        boolean hasBottom = false;
        boolean isDress = false;

        for (String category : categories) {
            String cat = category.toUpperCase();
            if (cat.contains("DRESS")) {
                isDress = true;
                break;
            }
            if (!hasTop && (cat.contains("TSHIRT") || cat.contains("SHIRT") || cat.contains("HOODIE") ||
                    cat.contains("JACKET") || cat.contains("CROP") || cat.contains("TOP"))) {
                hasTop = true;
            }
            if (!hasBottom && (cat.contains("PANTS") || cat.contains("SHORTS") || cat.contains("SKIRT"))) {
                hasBottom = true;
            }
        }

        // Handle dress case
        if (isDress) {
            return "dress";
        }

        // Build garment parts based on top + bottom
        if (hasTop && hasBottom) {
            // Need specific combination - let's determine exact garment
            String topPart = null;
            String bottomPart = null;

            for (String category : categories) {
                String cat = category.toUpperCase();
                if (topPart == null && (cat.contains("TSHIRT") || cat.contains("SHIRT") || cat.contains("CROP") || cat.contains("TOP"))) {
                    topPart = mapCategoryToGarmentPart(cat);
                }
                if (bottomPart == null && (cat.contains("PANTS") || cat.contains("SHORTS") || cat.contains("SKIRT"))) {
                    bottomPart = mapCategoryToGarmentPart(cat);
                }
            }

            // Return combined parts
            if (topPart != null && bottomPart != null) {
                return topPart + "_" + bottomPart;
            }
            return "tshirt_pants";
        } else if (hasTop) {
            return "tshirt_pants";
        } else if (hasBottom) {
            return "tshirt_pants";
        }

        return "tshirt_pants";
    }

    /**
     * Calculate fit score based on body type and clothing categories
     */
    private Double calculateFitScore(String bodyType, List<String> categories) {
        // Simple heuristic: more category-specific = higher score
        double baseScore = 0.7;

        if (categories.size() >= 2) {
            baseScore += 0.15; // Full outfit bonus
        }

        // Body type specific matching
        if (bodyType != null) {
            switch (bodyType.toLowerCase()) {
                case "slim":
                    baseScore += 0.05;
                    break;
                case "broad":
                    baseScore += 0.05;
                    break;
                case "regular":
                case "curvy":
                    baseScore += 0.1;
                    break;
            }
        }

        return Math.min(baseScore, 1.0);
    }
}

