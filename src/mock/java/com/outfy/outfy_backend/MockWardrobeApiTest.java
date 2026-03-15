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
 * Mock API tests for Wardrobe endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Mock Wardrobe API Tests")
public class MockWardrobeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== CREATE WARDROBE ITEM TESTS ====================

    @Test
    @DisplayName("POST /api/v1/wardrobe - Success")
    void testCreateWardrobeItem_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("clothingItemId", 1L);
        request.put("isFavorite", true);
        request.put("notes", "Good for winter");

        mockMvc.perform(post("/api/v1/wardrobe")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("POST /api/v1/wardrobe - Missing clothingItemId")
    void testCreateWardrobeItem_MissingClothingItemId() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("isFavorite", true);

        mockMvc.perform(post("/api/v1/wardrobe")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/wardrobe - Without auth")
    void testCreateWardrobeItem_WithoutAuth() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("clothingItemId", 1L);

        mockMvc.perform(post("/api/v1/wardrobe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET WARDROBE ITEMS TESTS ====================

    @Test
    @DisplayName("GET /api/v1/wardrobe/{id} - Success")
    void testGetWardrobeItem_Success() throws Exception {
        mockMvc.perform(get("/api/v1/wardrobe/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/wardrobe - Get all")
    void testGetAllWardrobeItems() throws Exception {
        mockMvc.perform(get("/api/v1/wardrobe")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("GET /api/v1/wardrobe - Filter by favorite")
    void testGetWardrobeItemsByFavorite() throws Exception {
        mockMvc.perform(get("/api/v1/wardrobe")
                        .param("favorite", "true")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== UPDATE WARDROBE ITEM TESTS ====================

    @Test
    @DisplayName("PUT /api/v1/wardrobe/{id} - Success")
    void testUpdateWardrobeItem_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("isFavorite", false);
        request.put("notes", "Updated notes");

        mockMvc.perform(put("/api/v1/wardrobe/1")
                        .header("Authorization", "Bearer mock_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== DELETE WARDROBE ITEM TESTS ====================

    @Test
    @DisplayName("DELETE /api/v1/wardrobe/{id} - Success")
    void testDeleteWardrobeItem_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/wardrobe/1")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/wardrobe/{id} - Not found")
    void testDeleteWardrobeItem_NotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/wardrobe/99999")
                        .header("Authorization", "Bearer mock_token"))
                .andExpect(status().isNotFound());
    }
}

