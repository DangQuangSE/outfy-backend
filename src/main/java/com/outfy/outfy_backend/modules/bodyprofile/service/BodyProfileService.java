package com.outfy.outfy_backend.modules.bodyprofile.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.BodyGenerationGateway;
import com.outfy.outfy_backend.modules.bodyprofile.dto.request.CreateBodyProfileRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyProfileResponse;
import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyGenerationResultEntity;
import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyProfile;
import com.outfy.outfy_backend.modules.bodyprofile.repository.BodyGenerationResultRepository;
import com.outfy.outfy_backend.modules.bodyprofile.repository.BodyProfileRepository;
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
public class BodyProfileService {
    
    private static final Logger logger = LoggerFactory.getLogger(BodyProfileService.class);
    
    private final BodyProfileRepository bodyProfileRepository;
    private final BodyGenerationResultRepository bodyGenerationResultRepository;
    private final BodyGenerationGateway bodyGenerationGateway;
    private final ObjectMapper objectMapper;

    public BodyProfileService(
            BodyProfileRepository bodyProfileRepository,
            BodyGenerationResultRepository bodyGenerationResultRepository,
            BodyGenerationGateway bodyGenerationGateway,
            ObjectMapper objectMapper) {
        this.bodyProfileRepository = bodyProfileRepository;
        this.bodyGenerationResultRepository = bodyGenerationResultRepository;
        this.bodyGenerationGateway = bodyGenerationGateway;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public BodyProfileResponse createBodyProfile(CreateBodyProfileRequest request) {
        logger.info("Creating body profile for user: {}", request.getUserId());
        
        BodyProfile profile = new BodyProfile();
        profile.setUserId(request.getUserId());
        profile.setGender(request.getGender());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setChestCm(request.getChestCm());
        profile.setWaistCm(request.getWaistCm());
        profile.setHipCm(request.getHipCm());
        profile.setShoulderCm(request.getShoulderCm());
        profile.setInseamCm(request.getInseamCm());
        
        BodyProfile saved = bodyProfileRepository.save(profile);
        logger.info("Created body profile with id: {}", saved.getId());
        
        return toResponse(saved);
    }

    public BodyProfileResponse getBodyProfileById(Long id) {
        BodyProfile profile = bodyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BodyProfile", "id", id));
        return toResponse(profile);
    }

    public List<BodyProfileResponse> getBodyProfilesByUserId(Long userId) {
        return bodyProfileRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BodyGenerationResult generateAvatar(Long profileId) {
        logger.info("Generating avatar for profile id: {}", profileId);
        
        BodyProfile profile = bodyProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("BodyProfile", "id", profileId));
        
        // Call gateway to generate avatar
        BodyGenerationResult result = bodyGenerationGateway.generate(profileId);
        
        // Save result
        BodyGenerationResultEntity entity = new BodyGenerationResultEntity();
        entity.setBodyProfileId(profileId);
        entity.setBodyType(result.getBodyType());
        entity.setAvatarPresetCode(result.getAvatarPresetCode());
        entity.setPreviewUrl(result.getPreviewUrl());
        entity.setConfidence(result.getConfidence());
        
        try {
            String shapeParamsJson = objectMapper.writeValueAsString(result.getShapeParams());
            entity.setShapeParamsJson(shapeParamsJson);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing shape params", e);
        }
        
        bodyGenerationResultRepository.save(entity);
        logger.info("Generated avatar with preset: {}", result.getAvatarPresetCode());
        
        return result;
    }

    public BodyGenerationResult getAvatarResult(Long profileId) {
        BodyGenerationResultEntity entity = bodyGenerationResultRepository.findByBodyProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("BodyGenerationResult", "profileId", profileId));
        
        BodyGenerationResult result = new BodyGenerationResult();
        result.setBodyType(entity.getBodyType());
        result.setAvatarPresetCode(entity.getAvatarPresetCode());
        result.setPreviewUrl(entity.getPreviewUrl());
        result.setConfidence(entity.getConfidence());
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Double> shapeParams = objectMapper.readValue(
                    entity.getShapeParamsJson(),
                    Map.class);
            result.setShapeParams(shapeParams);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing shape params", e);
        }
        
        return result;
    }

    private BodyProfileResponse toResponse(BodyProfile profile) {
        BodyProfileResponse response = new BodyProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setGender(profile.getGender());
        response.setHeightCm(profile.getHeightCm());
        response.setWeightKg(profile.getWeightKg());
        response.setChestCm(profile.getChestCm());
        response.setWaistCm(profile.getWaistCm());
        response.setHipCm(profile.getHipCm());
        response.setShoulderCm(profile.getShoulderCm());
        response.setInseamCm(profile.getInseamCm());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
}

