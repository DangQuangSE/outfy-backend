package com.outfy.outfy_backend.modules.recommendation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_sessions")
public class RecommendationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "body_profile_id")
    private Long bodyProfileId;

    @Column(name = "occasion")
    private String occasion;

    @Column(name = "preference_json", columnDefinition = "TEXT")
    private String preferenceJson;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBodyProfileId() { return bodyProfileId; }
    public void setBodyProfileId(Long bodyProfileId) { this.bodyProfileId = bodyProfileId; }
    public String getOccasion() { return occasion; }
    public void setOccasion(String occasion) { this.occasion = occasion; }
    public String getPreferenceJson() { return preferenceJson; }
    public void setPreferenceJson(String preferenceJson) { this.preferenceJson = preferenceJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

