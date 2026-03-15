package com.outfy.outfy_backend.modules.clothing.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.ClothingAnalysisGateway;
import com.outfy.outfy_backend.modules.clothing.dto.request.CreateClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingItemResponse;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingAnalysisResultEntity;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
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
public class ClothingService {

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

        // Use mapper to convert request to entity
        ClothingItem item = clothingMapper.toEntity(request);
        // Set default sourceType if not provided
        if (item.getSourceType() == null || item.getSourceType().isBlank()) {
            item.setSourceType("UPLOAD");
        }

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

    @Transactional
    public ClothingAnalysisResult analyzeClothing(Long clothingId) {
        logger.info("Analyzing clothing item id: {}", clothingId);

        ClothingItem item = clothingItemRepository.findById(clothingId)
                .orElseThrow(() -> new ResourceNotFoundException("ClothingItem", "id", clothingId));

        // Call gateway to analyze
        ClothingAnalysisResult result = clothingAnalysisGateway.analyze(clothingId);

        // Save result
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
        logger.info("Analyzed clothing item with category: {}", result.getGarmentCategory());

        return result;
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
}

