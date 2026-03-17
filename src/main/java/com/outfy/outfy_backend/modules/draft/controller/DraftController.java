package com.outfy.outfy_backend.modules.draft.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.draft.dto.request.CreateDraftRequest;
import com.outfy.outfy_backend.modules.draft.dto.response.DraftResponse;
import com.outfy.outfy_backend.modules.draft.enums.DraftType;
import com.outfy.outfy_backend.modules.draft.interfaces.IDraftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/drafts")
public class DraftController {

    private final IDraftService draftService;

    public DraftController(IDraftService draftService) {
        this.draftService = draftService;
    }

    /**
     * Create a new draft
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DraftResponse>> createDraft(
            @Valid @RequestBody CreateDraftRequest request) {
        DraftResponse response = draftService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Draft created successfully", response));
    }

    /**
     * Get draft by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DraftResponse>> getDraft(@PathVariable Long id) {
        DraftResponse response = draftService.getDraftById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all drafts for a user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DraftResponse>>> getDrafts(
            @RequestParam Long userId,
            @RequestParam(required = false) DraftType type) {
        List<DraftResponse> responses;
        if (type != null) {
            responses = draftService.getDraftsByUserIdAndType(userId, type);
        } else {
            responses = draftService.getDraftsByUserId(userId);
        }
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Analyze a draft
     */
    @PostMapping("/{id}/analyze")
    public ResponseEntity<ApiResponse<DraftResponse>> analyzeDraft(@PathVariable Long id) {
        DraftResponse response = draftService.analyzeDraft(id);
        return ResponseEntity.ok(ApiResponse.success("Draft analyzed successfully", response));
    }

    /**
     * Re-analyze a draft
     */
    @PostMapping("/{id}/reanalyze")
    public ResponseEntity<ApiResponse<DraftResponse>> reAnalyzeDraft(@PathVariable Long id) {
        DraftResponse response = draftService.reAnalyzeDraft(id);
        return ResponseEntity.ok(ApiResponse.success("Draft re-analyzed successfully", response));
    }

    /**
     * Delete a draft
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDraft(
            @PathVariable Long id,
            @RequestParam Long userId) {
        draftService.deleteDraft(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Draft deleted successfully", null));
    }
}

