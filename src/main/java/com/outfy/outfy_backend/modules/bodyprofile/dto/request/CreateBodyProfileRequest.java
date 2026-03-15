package com.outfy.outfy_backend.modules.bodyprofile.dto.request;

import jakarta.validation.constraints.*;

public class CreateBodyProfileRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double heightCm;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weightKg;

    @NotNull(message = "Chest measurement is required")
    @Positive(message = "Chest must be positive")
    private Double chestCm;

    @NotNull(message = "Waist measurement is required")
    @Positive(message = "Waist must be positive")
    private Double waistCm;

    @NotNull(message = "Hip measurement is required")
    @Positive(message = "Hip must be positive")
    private Double hipCm;

    @NotNull(message = "Shoulder measurement is required")
    @Positive(message = "Shoulder must be positive")
    private Double shoulderCm;

    @NotNull(message = "Inseam measurement is required")
    @Positive(message = "Inseam must be positive")
    private Double inseamCm;

    // Getters and Setters
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
}

