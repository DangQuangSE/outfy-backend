package com.outfy.outfy_backend.modules.clothing.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.ClothingAnalysisGateway;
import com.outfy.outfy_backend.modules.clothing.dto.request.AnalyzeClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.request.ConfirmClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.request.CreateClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingItemResponse;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingAnalysisResultEntity;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
import com.outfy.outfy_backend.modules.clothing.enums.ClothingItemStatus;
import com.outfy.outfy_backend.modules.clothing.interfaces.IClothingAnalysisService;
import com.outfy.outfy_backend.modules.clothing.mapper.ClothingMapper;
import com.outfy.outfy_backend.modules.clothing.repository.ClothingAnalysisResultRepository;
import com.outfy.outfy_backend.modules.clothing.repository.ClothingItemRepository;
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
public class ClothingService implements IClothingAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(ClothingService.class);

    private final ClothingItemRepository clothingItemRepository;
    private final ClothingAnalysisResultRepository clothingAnalysisResultRepository;
    private final ClothingAnalysisGateway clothingAnalysisGateway;
    private final ObjectMapper objectMapper;
    private final ClothingMapper clothingMapper;

    public ClothingService(
            ClothingItemRepository clothingItemRepository,
            ClothingAnalysisResultRepository clothingAnalysisResultRepository,
            ClothingAnalysisGateway clothingAnalysisGateway,
            ObjectMapper objectMapper,
            ClothingMapper clothingMapper) {
        this.clothingItemRepository = clothingItemRepository;
        this.clothingAnalysisResultRepository = clothingAnalysisResultRepository;
        this.clothingAnalysisGateway = clothingAnalysisGateway;
        this.objectMapper = objectMapper;
        this.clothingMapper = clothingMapper;
    }

    @Transactional
    public ClothingItemResponse createClothingItem(CreateClothingRequest request) {
        logger.info("Creating clothing item for user: {}", request.getUserId());

        ClothingItem item = clothingMapper.toEntity(request);
        if (item.getSourceType() == null || item.getSourceType().isBlank()) {
            item.setSourceType("UPLOAD");
        }
        item.setStatus(ClothingItemStatus.CREATED);

        ClothingItem saved = clothingItemRepository.save(item);
        logger.info("Created clothing item with id: {}", saved.getId());

        return clothingMapper.toResponse(saved);
    }

    public ClothingItemResponse getClothingItemById(Long id) {
        ClothingItem item = clothingItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingItem", "id", id));
        return clothingMapper.toResponse(item);
    }

    public List<ClothingItemResponse> getClothingItemsByUserId(Long userId) {
        return clothingItemRepository.findByUserId(userId).stream()
                .map(clothingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get wardrobe items - only CONFIRMED items
     */
    public List<ClothingItemResponse> getWardrobeItems(Long userId) {
        return clothingItemRepository.findByUserIdAndStatus(userId, ClothingItemStatus.CONFIRMED).stream()
                .map(clothingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClothingAnalysisResult analyzeClothing(Long clothingId) {
        logger.info("Analyzing clothing item id: {}", clothingId);

        ClothingItem item = clothingItemRepository.findById(clothingId)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingItem", "id", clothingId));

        // Update status to ANALYZING
        item.setStatus(ClothingItemStatus.ANALYZING);
        clothingItemRepository.save(item);

        try {
            ClothingAnalysisResult result = clothingAnalysisGateway.analyze(clothingId);

            ClothingAnalysisResultEntity entity = new ClothingAnalysisResultEntity();
            entity.setClothingItemId(clothingId);
            entity.setGarmentCategory(result.getGarmentCategory());
            entity.setTemplateCode(result.getTemplateCode());
            entity.setPreviewUrl(result.getPreviewUrl());

            try {
                entity.setAttributesJson(objectMapper.writeValueAsString(result.getAttributes()));
                entity.setGarmentParametersJson(objectMapper.writeValueAsString(result.getGarmentParameters()));
            } catch (JsonProcessingException e) {
                logger.error("Error serializing clothing attributes", e);
            }

            clothingAnalysisResultRepository.save(entity);

            // Update item with analysis result
            item.setGarmentCategory(result.getGarmentCategory());
            item.setTemplateCode(result.getTemplateCode());
            item.setModelUrl(result.getPreviewUrl());
            item.setPreviewUrl(result.getPreviewUrl());
            item.setStatus(ClothingItemStatus.ANALYZED);

            clothingItemRepository.save(item);
            logger.info("Analyzed clothing item with category: {}", result.getGarmentCategory());

            // Set clothingItemId for frontend to use when adding to wardrobe
            result.setClothingItemId(item.getId());

            return result;
        } catch (Exception e) {
            logger.error("Error analyzing clothing item", e);
            item.setStatus(ClothingItemStatus.FAILED);
            clothingItemRepository.save(item);
            throw new RuntimeException("Failed to analyze clothing: " + e.getMessage());
        }
    }

    public ClothingAnalysisResult getAnalysisResult(Long clothingId) {
        ClothingAnalysisResultEntity entity = clothingAnalysisResultRepository.findByClothingItemId(clothingId)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingAnalysisResult", "clothingId", clothingId));

        ClothingAnalysisResult result = new ClothingAnalysisResult();
        result.setGarmentCategory(entity.getGarmentCategory());
        result.setTemplateCode(entity.getTemplateCode());
        result.setPreviewUrl(entity.getPreviewUrl());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> attributes = objectMapper.readValue(
                    entity.getAttributesJson(), Map.class);
            result.setAttributes(attributes);

            @SuppressWarnings("unchecked")
            Map<String, Object> garmentParameters = objectMapper.readValue(
                    entity.getGarmentParametersJson(), Map.class);
            result.setGarmentParameters(garmentParameters);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing clothing attributes", e);
        }

        return result;
    }

    /**
     * Confirm clothing item to add to wardrobe
     */
    @Transactional
    public ClothingItemResponse confirmClothingItem(Long clothingId, ConfirmClothingRequest request) {
        logger.info("Confirming clothing item id: {}", clothingId);

        ClothingItem item = clothingItemRepository.findById(clothingId)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingItem", "id", clothingId));

        // Check if item has been analyzed
        if (item.getStatus() != ClothingItemStatus.ANALYZED) {
            throw new IllegalStateException("Item must be analyzed before confirmation");
        }

        // Update item with final metadata
        if (request.getName() != null && !request.getName().isBlank()) {
            item.setName(request.getName());
        }
        if (request.getColor() != null && !request.getColor().isBlank()) {
            item.setColor(request.getColor());
        }

        item.setStatus(ClothingItemStatus.CONFIRMED);

        ClothingItem saved = clothingItemRepository.save(item);
        logger.info("Confirmed clothing item id: {} - added to wardrobe", clothingId);

        return clothingMapper.toResponse(saved);
    }

    /**
     * Delete a clothing item (only DRAFT or FAILED status allowed)
     */
    @Transactional
    public void deleteClothingItem(Long id, Long userId) {
        logger.info("Deleting clothing item id: {} for user: {}", id, userId);

        ClothingItem item = clothingItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingItem", "id", id));

        // Verify ownership
        if (!item.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Item does not belong to user");
        }

        // Only allow delete for certain statuses
        if (item.getStatus() == ClothingItemStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot delete confirmed wardrobe items");
        }

        clothingItemRepository.delete(item);
        logger.info("Deleted clothing item id: {}", id);
    }

    /**
     * Re-analyze a clothing item
     */
    @Transactional
    public ClothingAnalysisResult reAnalyzeClothing(Long clothingId) {
        logger.info("Re-analyzing clothing item id: {}", clothingId);

        ClothingItem item = clothingItemRepository.findById(clothingId)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingItem", "id", clothingId));

        // Only allow re-analyze for ANALYZED or FAILED items
        if (item.getStatus() != ClothingItemStatus.ANALYZED &&
            item.getStatus() != ClothingItemStatus.FAILED) {
            throw new IllegalStateException("Only analyzed or failed items can be re-analyzed");
        }

        // Reset to CREATED and analyze again
        item.setStatus(ClothingItemStatus.CREATED);
        clothingItemRepository.save(item);

        return analyzeClothing(clothingId);
    }

    /**
     * Analyze clothing directly from image (for demo without database)
     * Creates a ClothingItem with ANALYZED status
     */
    public ClothingAnalysisResult analyzeClothingDirect(AnalyzeClothingRequest request) {
        logger.info("Analyzing clothing directly from image - url: {}, filename: {}",
                request.getImageUrl(), request.getFileName());

        // First create a ClothingItem with CREATED status
        ClothingItem item = new ClothingItem();
        item.setUserId(request.getUserId());
        item.setImageUrl(request.getImageUrl());
        item.setFileName(request.getFileName());
        item.setSourceType("UPLOAD");
        item.setName(request.getName() != null ? request.getName() : request.getFileName());
        item.setStatus(ClothingItemStatus.CREATED);

        ClothingItem savedItem = clothingItemRepository.save(item);
        logger.info("Created clothing item with id: {}", savedItem.getId());

        // Then analyze it
        return analyzeClothing(savedItem.getId());
    }
}

