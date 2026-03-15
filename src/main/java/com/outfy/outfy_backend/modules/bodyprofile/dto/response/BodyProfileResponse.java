package com.outfy.outfy_backend.modules.bodyprofile.dto.response;

import java.time.LocalDateTime;

public class BodyProfileResponse {
    private Long id;
    private Long userId;
    private String gender;
    private Double heightCm;
    private Double weightKg;
    private Double chestCm;
    private Double waistCm;
    private Double hipCm;
    private Double shoulderCm;
    private Double inseamCm;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public Double getChestCm() { return chestCm; }
    public void setChestCm(Double chestCm) { this.chestCm = chestCm; }
    public Double getWaistCm() { return waistCm; }
    public void setWaistCm(Double waistCm) { this.waistCm = waistCm; }
    public Double getHipCm() { return hipCm; }
    public void setHipCm(Double hipCm) { this.hipCm = hipCm; }
    public Double getShoulderCm() { return shoulderCm; }
    public void setShoulderCm(Double shoulderCm) { this.shoulderCm = shoulderCm; }
    public Double getInseamCm() { return inseamCm; }
    public void setInseamCm(Double inseamCm) { this.inseamCm = inseamCm; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

