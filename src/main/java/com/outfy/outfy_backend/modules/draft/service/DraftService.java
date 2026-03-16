package com.outfy.outfy_backend.modules.draft.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outfy.outfy_backend.common.exception.ResourceNotFoundException;
import com.outfy.outfy_backend.infrastructure.external.ClothingAnalysisGateway;
import com.outfy.outfy_backend.modules.draft.dto.request.CreateDraftRequest;
import com.outfy.outfy_backend.modules.draft.dto.response.DraftResponse;
import com.outfy.outfy_backend.modules.draft.entity.Draft;
import com.outfy.outfy_backend.modules.draft.enums.DraftStatus;
import com.outfy.outfy_backend.modules.draft.enums.DraftType;
import com.outfy.outfy_backend.modules.draft.interfaces.IDraftService;
import com.outfy.outfy_backend.modules.draft.mapper.DraftMapper;
import com.outfy.outfy_backend.modules.draft.repository.DraftRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DraftService implements IDraftService {

    private static final Logger logger = LoggerFactory.getLogger(DraftService.class);

    private final DraftRepository draftRepository;
    private final DraftMapper draftMapper;
    private final ClothingAnalysisGateway clothingAnalysisGateway;
    private final ObjectMapper objectMapper;

    public DraftService(
            DraftRepository draftRepository,
            DraftMapper draftMapper,
            ClothingAnalysisGateway clothingAnalysisGateway,
            ObjectMapper objectMapper) {
        this.draftRepository = draftRepository;
        this.draftMapper = draftMapper;
        this.clothingAnalysisGateway = clothingAnalysisGateway;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public DraftResponse createDraft(CreateDraftRequest request) {
        logger.info("Creating draft for user: {}, type: {}", request.getUserId(), request.getDraftType());

        Draft draft = draftMapper.toEntity(request);
        draft.setStatus(DraftStatus.DRAFT);

        Draft saved = draftRepository.save(draft);
        logger.info("Created draft with id: {}", saved.getId());

        return draftMapper.toResponse(saved);
    }

    @Override
    public DraftResponse getDraftById(Long id) {
        Draft draft = draftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft", "id", id));
        return draftMapper.toResponse(draft);
    }

    @Override
    public List<DraftResponse> getDraftsByUserId(Long userId) {
        return draftRepository.findByUserId(userId).stream()
                .map(draftMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DraftResponse> getDraftsByUserIdAndType(Long userId, DraftType draftType) {
        return draftRepository.findByUserIdAndDraftType(userId, draftType).stream()
                .map(draftMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DraftResponse analyzeDraft(Long draftId) {
        logger.info("Analyzing draft id: {}", draftId);

        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft", "id", draftId));

        // Update status to ANALYZING
        draft.setStatus(DraftStatus.ANALYZING);
        draftRepository.save(draft);

        // For CLOTH type, use clothing analysis gateway
        if (draft.getDraftType() == DraftType.CLOTH) {
            return analyzeClothDraft(draft);
        }

        // BODY type - to be implemented later
        logger.warn("Body draft analysis not yet implemented");
        draft.setStatus(DraftStatus.DRAFT);
        draftRepository.save(draft);
        return draftMapper.toResponse(draft);
    }

    private DraftResponse analyzeClothDraft(Draft draft) {
        try {
            // Build input data JSON
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("imageUrl", draft.getImageUrl());
            inputData.put("fileName", draft.getFileName());
            draft.setInputDataJson(objectMapper.writeValueAsString(inputData));

            // Call clothing analysis gateway
            var result = clothingAnalysisGateway.analyzeFromImage(
                    draft.getImageUrl(),
                    draft.getFileName()
            );

            // Update draft with analysis result
            draft.setGarmentCategory(result.getGarmentCategory());
            draft.setTemplateCode(result.getTemplateCode());
            draft.setModelUrl(result.getPreviewUrl());
            draft.setPreviewUrl(result.getPreviewUrl());
            draft.setStatus(DraftStatus.ANALYZED);

            // Save analysis result JSON
            Map<String, Object> analysisResult = new HashMap<>();
            analysisResult.put("garmentCategory", result.getGarmentCategory());
            analysisResult.put("templateCode", result.getTemplateCode());
            analysisResult.put("previewUrl", result.getPreviewUrl());
            analysisResult.put("attributes", result.getAttributes());
            analysisResult.put("garmentParameters", result.getGarmentParameters());
            analysisResult.put("confidence", result.getConfidence());
            draft.setAnalysisResultJson(objectMapper.writeValueAsString(analysisResult));

            // Extract color from attributes if available
            if (result.getAttributes() != null && result.getAttributes().get("color") != null) {
                draft.setColor(result.getAttributes().get("color").toString());
            }

            Draft saved = draftRepository.save(draft);
            logger.info("Analyzed cloth draft with category: {}", draft.getGarmentCategory());

            return draftMapper.toResponse(saved);

        } catch (Exception e) {
            logger.error("Error analyzing cloth draft", e);
            draft.setStatus(DraftStatus.DRAFT);
            draftRepository.save(draft);
            throw new RuntimeException("Failed to analyze draft: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public DraftResponse reAnalyzeDraft(Long draftId) {
        logger.info("Re-analyzing draft id: {}", draftId);

        Draft draft = draftRepository.findById(draftId)
                .orElseThrow(() -> new ResourceNotFoundException("Draft", "id", draftId));

        // Only allow re-analyze for ANALYZED drafts
        if (draft.getStatus() != DraftStatus.ANALYZED) {
            throw new IllegalStateException("Only analyzed drafts can be re-analyzed");
        }

        // Reset to DRAFT and analyze again
        draft.setStatus(DraftStatus.DRAFT);
        draftRepository.save(draft);

        return analyzeDraft(draftId);
    }

    @Override
    @Transactional
    public void deleteDraft(Long id, Long userId) {
        logger.info("Deleting draft id: {} for user: {}", id, userId);

        Draft draft = draftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Draft", "id", id));

        // Verify ownership
        if (!draft.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Draft does not belong to user");
        }

        draftRepository.delete(draft);
        logger.info("Deleted draft id: {}", id);
    }
}

