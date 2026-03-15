package com.outfy.outfy_backend.modules.auth.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.auth.dto.request.LoginRequest;
import com.outfy.outfy_backend.modules.auth.dto.request.RefreshTokenRequest;
import com.outfy.outfy_backend.modules.auth.dto.request.RegisterRequest;
import com.outfy.outfy_backend.modules.auth.dto.response.AuthResponse;
import com.outfy.outfy_backend.modules.auth.dto.response.UserResponse;
import com.outfy.outfy_backend.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.API_V1 + "/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        // Extract user ID from token - for now we'll assume the user ID is available
        // In a real implementation, this would be extracted from the JWT
        // This is a placeholder - actual implementation would need to parse the token
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        // This would extract user ID from token and get user details
        // Placeholder implementation
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

