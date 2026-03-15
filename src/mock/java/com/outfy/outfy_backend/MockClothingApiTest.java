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
 * Mock API tests for Clothing endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Mock Clothing API Tests")
public class MockClothingApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE CLOTHING ITEM TESTS ====================

    @Test
    @DisplayName("POST /api/v1/clothing - Success")
    void testCreateClothingItem_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Black Hoodie");
        request.put("category", "OUTERWEAR");
        request.put("color", "BLACK");
        request.put("style", "CASUAL");
        request.put("imageUrl", "https://example.com/hoodie.jpg");

        mockMvc.perform(post("/api/v1/clothing")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("POST /api/v1/clothing - Missing required fields")
    void testCreateClothingItem_MissingFields() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Black Hoodie");
        // Missing category, color, etc.

        mockMvc.perform(post("/api/v1/clothing")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/clothing - Without auth")
    void testCreateClothingItem_WithoutAuth() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Black Hoodie");
        request.put("category", "OUTERWEAR");

        mockMvc.perform(post("/api/v1/clothing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET CLOTHING ITEMS TESTS ====================

    @Test
    @DisplayName("GET /api/v1/clothing/{id} - Success")
    void testGetClothingItem_Success() throws Exception {
        mockMvc.perform(get("/api/v1/clothing/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/clothing - Get all")
    void testGetAllClothingItems() throws Exception {
        mockMvc.perform(get("/api/v1/clothing")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/clothing - Filter by category")
    void testGetClothingItemsByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/clothing")
                        .param("category", "OUTERWEAR")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== UPDATE CLOTHING ITEM TESTS ====================

    @Test
    @DisplayName("PUT /api/v1/clothing/{id} - Success")
    void testUpdateClothingItem_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Updated Black Hoodie");
        request.put("color", "DARK_GRAY");

        mockMvc.perform(put("/api/v1/clothing/1")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== DELETE CLOTHING ITEM TESTS ====================

    @Test
    @DisplayName("DELETE /api/v1/clothing/{id} - Success")
    void testDeleteClothingItem_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/clothing/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/clothing/{id} - Not found")
    void testDeleteClothingItem_NotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/clothing/99999")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isNotFound());
    }
}

