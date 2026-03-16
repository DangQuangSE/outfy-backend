package com.outfy.outfy_backend.modules.tryon.dto.request;

public class UpdateTryOnSessionRequest {

    private String size;
    private String fitType;
    private String sleeveLength;
    private String avatarId;

    // Getters and Setters
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }
    public String getSleeveLength() { return sleeveLength; }
    public void setSleeveLength(String sleeveLength) { this.sleeveLength = sleeveLength; }
    public String getAvatarId() { return avatarId; }
    public void setAvatarId(String avatarId) { this.avatarId = avatarId; }
}

