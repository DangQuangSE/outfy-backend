package com.outfy.outfy_backend.modules.tryon.service;

import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.TryOnGateway;
import com.outfy.outfy_backend.modules.tryon.dto.request.CreateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnSessionResponse;
import com.outfy.outfy_backend.modules.tryon.entity.TryOnResultEntity;
import com.outfy.outfy_backend.modules.tryon.entity.TryOnSession;
import com.outfy.outfy_backend.modules.tryon.mapper.TryOnMapper;
import com.outfy.outfy_backend.modules.tryon.repository.TryOnResultRepository;
import com.outfy.outfy_backend.modules.tryon.repository.TryOnSessionRepository;
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
public class TryOnService {

    private static final Logger logger = LoggerFactory.getLogger(TryOnService.class);

    private final TryOnSessionRepository tryOnSessionRepository;
    private final TryOnResultRepository tryOnResultRepository;
    private final TryOnGateway tryOnGateway;
    private final ObjectMapper objectMapper;
    private final TryOnMapper tryOnMapper;

    public TryOnService(
            TryOnSessionRepository tryOnSessionRepository,
            TryOnResultRepository tryOnResultRepository,
            TryOnGateway tryOnGateway,
            ObjectMapper objectMapper,
            TryOnMapper tryOnMapper) {
        this.tryOnSessionRepository = tryOnSessionRepository;
        this.tryOnResultRepository = tryOnResultRepository;
        this.tryOnGateway = tryOnGateway;
        this.objectMapper = objectMapper;
        this.tryOnMapper = tryOnMapper;
    }

    @Transactional
    public TryOnSessionResponse createTryOnSession(CreateTryOnSessionRequest request) {
        logger.info("Creating try-on session for user: {}, bodyProfile: {}, clothing: {}",
                request.getUserId(), request.getBodyProfileId(), request.getClothingItemId());

        // Use mapper to convert request to entity
        TryOnSession session = tryOnMapper.toEntity(request);
        session.setStatus("PENDING");

        TryOnSession saved = tryOnSessionRepository.save(session);
        logger.info("Created try-on session with id: {}", saved.getId());

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

        // Call gateway to generate try-on result
        TryOnResult result = tryOnGateway.generate(session.getBodyProfileId(), session.getClothingItemId());

        // Save result
        TryOnResultEntity entity = new TryOnResultEntity();
        entity.setTryonSessionId(sessionId);
        entity.setPreviewUrl(result.getPreviewUrl());
        entity.setFitScore(result.getFitScore());
        entity.setResultNote(result.getNote());

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
}

