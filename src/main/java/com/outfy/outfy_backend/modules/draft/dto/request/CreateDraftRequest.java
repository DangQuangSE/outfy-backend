package com.outfy.outfy_backend.modules.draft.dto.request;

import com.outfy.outfy_backend.modules.draft.enums.DraftType;
import jakarta.validation.constraints.NotNull;

public class CreateDraftRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Draft type is required")
    private DraftType draftType;

    private String name;
    private String imageUrl;
    private String fileName;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public DraftType getDraftType() { return draftType; }
    public void setDraftType(DraftType draftType) { this.draftType = draftType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}

