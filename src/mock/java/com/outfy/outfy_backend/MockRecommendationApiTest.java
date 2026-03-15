package com.outfy.outfy_backend.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Mock API tests for Recommendation endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Mock Recommendation API Tests")
public class MockRecommendationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE RECOMMENDATION SESSION TESTS ====================

    @Test
    @DisplayName("POST /api/v1/recommendations - Success")
    void testCreateRecommendationSession_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("bodyProfileId", 1L);
        request.put("occasion", "CASUAL");
        request.put("weather", "SUNNY");
        request.put("style", "CASUAL");
        request.put("clothingItemIds", new ArrayList<Long>() {{ add(1L); add(2L); add(3L); }});

        mockMvc.perform(post("/api/v1/recommendations")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("POST /api/v1/recommendations - Missing occasion")
    void testCreateRecommendationSession_MissingOccasion() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("bodyProfileId", 1L);
        request.put("weather", "SUNNY");
        request.put("clothingItemIds", new ArrayList<Long>() {{ add(1L); }});

        mockMvc.perform(post("/api/v1/recommendations")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/recommendations - Without auth")
    void testCreateRecommendationSession_WithoutAuth() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("bodyProfileId", 1L);
        request.put("occasion", "CASUAL");

        mockMvc.perform(post("/api/v1/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET RECOMMENDATION SESSION TESTS ====================

    @Test
    @DisplayName("GET /api/v1/recommendations/{id} - Success")
    void testGetRecommendationSession_Success() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/recommendations/{id} - Not found")
    void testGetRecommendationSession_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations/99999")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/recommendations - Get all")
    void testGetAllRecommendationSessions() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== GET RECOMMENDATION RESULTS TESTS ====================

    @Test
    @DisplayName("GET /api/v1/recommendations/{id}/results - Success")
    void testGetRecommendationResults_Success() throws Exception {
        mockMvc.perform(get("/api/v1/recommendations/1/results")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== DELETE RECOMMENDATION SESSION TESTS ====================

    @Test
    @DisplayName("DELETE /api/v1/recommendations/{id} - Success")
    void testDeleteRecommendationSession_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/recommendations/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }
}

