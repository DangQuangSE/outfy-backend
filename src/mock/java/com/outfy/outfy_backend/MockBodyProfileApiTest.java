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
 * Mock API tests for Body Profile endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Mock Body Profile API Tests")
public class MockBodyProfileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE BODY PROFILE TESTS ====================

    @Test
    @DisplayName("POST /api/v1/body-profiles - Success")
    void testCreateBodyProfile_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("gender", "MALE");
        request.put("height", 175.0);
        request.put("weight", 70.0);
        request.put("chest", 95.0);
        request.put("waist", 80.0);
        request.put("hips", 95.0);
        request.put("shoulderWidth", 45.0);

        mockMvc.perform(post("/api/v1/body-profiles")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("POST /api/v1/body-profiles - Invalid height")
    void testCreateBodyProfile_InvalidHeight() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("gender", "MALE");
        request.put("height", -10.0); // Invalid negative height

        mockMvc.perform(post("/api/v1/body-profiles")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/body-profiles - Without auth")
    void testCreateBodyProfile_WithoutAuth() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("gender", "MALE");
        request.put("height", 175.0);

        mockMvc.perform(post("/api/v1/body-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET BODY PROFILE TESTS ====================

    @Test
    @DisplayName("GET /api/v1/body-profiles/{id} - Success")
    void testGetBodyProfile_Success() throws Exception {
        mockMvc.perform(get("/api/v1/body-profiles/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/body-profiles/{id} - Not found")
    void testGetBodyProfile_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/body-profiles/99999")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/body-profiles - Get all")
    void testGetAllBodyProfiles() throws Exception {
        mockMvc.perform(get("/api/v1/body-profiles")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== UPDATE BODY PROFILE TESTS ====================

    @Test
    @DisplayName("PUT /api/v1/body-profiles/{id} - Success")
    void testUpdateBodyProfile_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("height", 180.0);
        request.put("weight", 75.0);

        mockMvc.perform(put("/api/v1/body-profiles/1")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/body-profiles/{id} - Not found")
    void testUpdateBodyProfile_NotFound() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("height", 180.0);

        mockMvc.perform(put("/api/v1/body-profiles/99999")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE BODY PROFILE TESTS ====================

    @Test
    @DisplayName("DELETE /api/v1/body-profiles/{id} - Success")
    void testDeleteBodyProfile_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/body-profiles/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/body-profiles/{id} - Not found")
    void testDeleteBodyProfile_NotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/body-profiles/99999")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isNotFound());
    }
}

