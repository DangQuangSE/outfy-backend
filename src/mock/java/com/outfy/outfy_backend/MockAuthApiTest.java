package com.outfy.outfy_backend.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
 * Mock API tests for Authentication endpoints
 * Tests without database connection
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Mock Auth API Tests")
public class MockAuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("POST /api/v1/auth/register - Success")
    void testRegister_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "test@example.com");
        request.put("password", "password123");
        request.put("fullName", "Test User");
        request.put("phone", "0912345678");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Invalid email")
    void testRegister_InvalidEmail() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "invalid-email");
        request.put("password", "password123");
        request.put("fullName", "Test User");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Missing required fields")
    void testRegister_MissingFields() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "test@example.com");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("POST /api/v1/auth/login - Success")
    void testLogin_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "user@example.com");
        request.put("password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Invalid credentials")
    void testLogin_InvalidCredentials() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "wrong@example.com");
        request.put("password", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Missing fields")
    void testLogin_MissingFields() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("email", "test@example.com");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== REFRESH TOKEN TESTS ====================

    @Test
    @DisplayName("POST /api/v1/auth/refresh - Success")
    void testRefreshToken_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("refreshToken", "valid_refresh_token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh - Invalid token")
    void testRefreshToken_InvalidToken() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("refreshToken", "invalid_token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== LOGOUT TESTS ====================

    @Test
    @DisplayName("POST /api/v1/auth/logout - Without auth header")
    void testLogout_WithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET CURRENT USER TEST ====================

    @Test
    @DisplayName("GET /api/v1/auth/me - Without auth")
    void testGetCurrentUser_WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}

