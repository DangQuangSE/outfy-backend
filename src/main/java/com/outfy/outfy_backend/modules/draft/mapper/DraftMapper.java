package com.outfy.outfy_backend.modules.draft.mapper;

import com.outfy.outfy_backend.modules.draft.dto.request.CreateDraftRequest;
import com.outfy.outfy_backend.modules.draft.dto.response.DraftResponse;
import com.outfy.outfy_backend.modules.draft.entity.Draft;
import org.springframework.stereotype.Component;

@Component
public class DraftMapper {

    public Draft toEntity(CreateDraftRequest request) {
        Draft draft = new Draft();
        draft.setUserId(request.getUserId());
        draft.setDraftType(request.getDraftType());
        draft.setName(request.getName());
        draft.setImageUrl(request.getImageUrl());
        draft.setFileName(request.getFileName());
        return draft;
    }

    public DraftResponse toResponse(Draft draft) {
        DraftResponse response = new DraftResponse();
        response.setId(draft.getId());
        response.setUserId(draft.getUserId());
        response.setDraftType(draft.getDraftType());
        response.setSourceItemId(draft.getSourceItemId());
        response.setName(draft.getName());
        response.setImageUrl(draft.getImageUrl());
        response.setFileName(draft.getFileName());
        response.setStatus(draft.getStatus());
        response.setGarmentCategory(draft.getGarmentCategory());
        response.setTemplateCode(draft.getTemplateCode());
        response.setModelUrl(draft.getModelUrl());
        response.setPreviewUrl(draft.getPreviewUrl());
        response.setColor(draft.getColor());
        response.setCreatedAt(draft.getCreatedAt());
        response.setUpdatedAt(draft.getUpdatedAt());
        return response;
    }
}

