package com.outfy.outfy_backend.modules.draft.interfaces;

import com.outfy.outfy_backend.modules.draft.dto.request.CreateDraftRequest;
import com.outfy.outfy_backend.modules.draft.dto.response.DraftResponse;
import com.outfy.outfy_backend.modules.draft.enums.DraftType;

import java.util.List;

public interface IDraftService {

    DraftResponse createDraft(CreateDraftRequest request);

    DraftResponse getDraftById(Long id);

    List<DraftResponse> getDraftsByUserId(Long userId);

    List<DraftResponse> getDraftsByUserIdAndType(Long userId, DraftType draftType);

    DraftResponse analyzeDraft(Long draftId);

    DraftResponse reAnalyzeDraft(Long draftId);

    void deleteDraft(Long id, Long userId);
}

