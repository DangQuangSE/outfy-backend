package com.outfy.outfy_backend.modules.bodyprofile.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.IBodyGenerationGateway;
import com.outfy.outfy_backend.modules.bodyprofile.dto.request.CreateBodyProfileRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.request.GenerateAvatarRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyProfileResponse;
import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyGenerationResultEntity;
import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyProfile;
import com.outfy.outfy_backend.modules.bodyprofile.interfaces.IBodyProfileService;
import com.outfy.outfy_backend.modules.bodyprofile.mapper.BodyProfileMapper;
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
public class BodyProfileService implements IBodyProfileService {

    private static final Logger logger = LoggerFactory.getLogger(BodyProfileService.class);

    private final BodyProfileRepository bodyProfileRepository;
    private final BodyGenerationResultRepository bodyGenerationResultRepository;
    private final IBodyGenerationGateway bodyGenerationGateway;
    private final ObjectMapper objectMapper;
    private final BodyProfileMapper bodyProfileMapper;

    public BodyProfileService(
            BodyProfileRepository bodyProfileRepository,
            BodyGenerationResultRepository bodyGenerationResultRepository,
            IBodyGenerationGateway bodyGenerationGateway,
            ObjectMapper objectMapper,
            BodyProfileMapper bodyProfileMapper) {
        this.bodyProfileRepository = bodyProfileRepository;
        this.bodyGenerationResultRepository = bodyGenerationResultRepository;
        this.bodyGenerationGateway = bodyGenerationGateway;
        this.objectMapper = objectMapper;
        this.bodyProfileMapper = bodyProfileMapper;
    }

    @Transactional
    public BodyProfileResponse createBodyProfile(CreateBodyProfileRequest request) {
        logger.info("Creating body profile for user: {}", request.getUserId());

        // Use mapper to convert request to entity
        BodyProfile profile = bodyProfileMapper.toEntity(request);

        BodyProfile saved = bodyProfileRepository.save(profile);
        logger.info("Created body profile with id: {}", saved.getId());

        return bodyProfileMapper.toResponse(saved);
    }

    public BodyProfileResponse getBodyProfileById(Long id) {
        BodyProfile profile = bodyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BodyProfile", "id", id));
        return bodyProfileMapper.toResponse(profile);
    }

    public List<BodyProfileResponse> getBodyProfilesByUserId(Long userId) {
        return bodyProfileRepository.findByUserId(userId).stream()
                .map(bodyProfileMapper::toResponse)
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
        entity.setModelUrl(result.getModelUrl());
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
        result.setModelUrl(entity.getModelUrl());
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

    /**
     * Generate avatar directly from measurements
     * If userId is provided, saves result to database
     */
    public BodyGenerationResult generateAvatarDirect(GenerateAvatarRequest request) {
        logger.info("Generating avatar directly from measurements - userId: {}, gender: {}, height: {}, weight: {}",
                request.getUserId(), request.getGender(), request.getHeightCm(), request.getWeightKg());

        // Call gateway to generate avatar from measurements
        BodyGenerationResult result = bodyGenerationGateway.generateFromMeasurements(
                request.getGender(),
                request.getHeightCm(),
                request.getWeightKg(),
                request.getChestCm(),
                request.getWaistCm(),
                request.getHipCm(),
                request.getShoulderCm(),
                request.getInseamCm()
        );

        logger.info("Generated avatar - bodyType: {}, preset: {}, modelUrl: {}",
                result.getBodyType(), result.getAvatarPresetCode(), result.getModelUrl());

        // Save to database if userId is provided
        if (request.getUserId() != null) {
            saveAvatarResult(request.getUserId(), result);
        }

        return result;
    }

    /**
     * Save avatar generation result to database
     */
    private void saveAvatarResult(Long userId, BodyGenerationResult result) {
        try {
            // First create or get body profile
            BodyProfile bodyProfile = bodyProfileRepository.findByUserId(userId)
                    .stream().findFirst()
                    .orElseGet(() -> {
                        // Create new body profile if not exists
                        BodyProfile newProfile = new BodyProfile();
                        newProfile.setUserId(userId);
                        newProfile.setGender(result.getBodyType().contains("female") ? "Female" : "Male");
                        newProfile.setHeightCm(170.0);
                        newProfile.setWeightKg(65.0);
                        newProfile.setChestCm(90.0);
                        newProfile.setWaistCm(75.0);
                        newProfile.setHipCm(95.0);
                        newProfile.setShoulderCm(42.0);
                        newProfile.setInseamCm(75.0);
                        return bodyProfileRepository.save(newProfile);
                    });

            // Save generation result
            BodyGenerationResultEntity entity = new BodyGenerationResultEntity();
            entity.setBodyProfileId(bodyProfile.getId());
            entity.setBodyType(result.getBodyType());
            entity.setAvatarPresetCode(result.getAvatarPresetCode());
            entity.setPreviewUrl(result.getPreviewUrl());
            entity.setModelUrl(result.getModelUrl());
            entity.setConfidence(result.getConfidence());

            try {
                String shapeParamsJson = objectMapper.writeValueAsString(result.getShapeParams());
                entity.setShapeParamsJson(shapeParamsJson);
            } catch (JsonProcessingException e) {
                logger.error("Error serializing shape params", e);
            }

            bodyGenerationResultRepository.save(entity);
            logger.info("Saved avatar result to database for userId: {}", userId);

        } catch (Exception e) {
            logger.error("Error saving avatar result to database", e);
            // Continue returning result even if save fails
        }
    }
}

