package com.outfy.outfy_backend.modules.bodyprofile.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_profiles")
public class BodyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String gender;

    @Column(name = "height_cm", nullable = false)
    private Double heightCm;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "chest_cm", nullable = false)
    private Double chestCm;

    @Column(name = "waist_cm", nullable = false)
    private Double waistCm;

    @Column(name = "hip_cm", nullable = false)
    private Double hipCm;

    @Column(name = "shoulder_cm", nullable = false)
    private Double shoulderCm;

    @Column(name = "inseam_cm", nullable = false)
    private Double inseamCm;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

