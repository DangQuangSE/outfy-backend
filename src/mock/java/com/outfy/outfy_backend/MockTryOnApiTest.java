package com.outfy.outfy_backend.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Mock API tests for TryOn endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Mock TryOn API Tests")
public class MockTryOnApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE TRYON SESSION TESTS ====================

    @Test
    @DisplayName("POST /api/v1/tryons - Success")
    void testCreateTryOnSession_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("bodyProfileId", 1L);
        request.put("clothingItemId", 1L);

        mockMvc.perform(post("/api/v1/tryons")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("POST /api/v1/tryons - Missing bodyProfileId")
    void testCreateTryOnSession_MissingBodyProfileId() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("clothingItemId", 1L);

        mockMvc.perform(post("/api/v1/tryons")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/tryons - Without auth")
    void testCreateTryOnSession_WithoutAuth() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("bodyProfileId", 1L);
        request.put("clothingItemId", 1L);

        mockMvc.perform(post("/api/v1/tryons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET TRYON SESSION TESTS ====================

    @Test
    @DisplayName("GET /api/v1/tryons/{id} - Success")
    void testGetTryOnSession_Success() throws Exception {
        mockMvc.perform(get("/api/v1/tryons/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/tryons/{id} - Not found")
    void testGetTryOnSession_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tryons/99999")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/tryons - Get all sessions")
    void testGetAllTryOnSessions() throws Exception {
        mockMvc.perform(get("/api/v1/tryons")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== GET TRYON RESULT TESTS ====================

    @Test
    @DisplayName("GET /api/v1/tryons/{id}/result - Success")
    void testGetTryOnResult_Success() throws Exception {
        mockMvc.perform(get("/api/v1/tryons/1/result")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/tryons/{id}/result - Not found")
    void testGetTryOnResult_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tryons/99999/result")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE TRYON SESSION TESTS ====================

    @Test
    @DisplayName("DELETE /api/v1/tryons/{id} - Success")
    void testDeleteTryOnSession_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/tryons/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }
}

