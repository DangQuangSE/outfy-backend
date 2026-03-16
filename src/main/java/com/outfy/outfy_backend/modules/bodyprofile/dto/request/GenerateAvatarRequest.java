package com.outfy.outfy_backend.modules.bodyprofile.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request DTO for direct avatar generation from measurements
 * Used for demo without database
 */
public class GenerateAvatarRequest {

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "(?i)male|female", message = "Gender must be Male or Female")
    private String gender;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    @Min(value = 100, message = "Height must be at least 100 cm")
    @Max(value = 250, message = "Height must not exceed 250 cm")
    private Double heightCm;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 200, message = "Weight must not exceed 200 kg")
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

