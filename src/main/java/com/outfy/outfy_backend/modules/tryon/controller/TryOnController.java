package com.outfy.outfy_backend.modules.tryon.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.tryon.dto.request.CreateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.TryOnFromWardrobeRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.UpdateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResultResponse;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnSessionResponse;
import com.outfy.outfy_backend.modules.tryon.service.TryOnService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/tryons")
public class TryOnController {

    private final TryOnService tryOnService;

    public TryOnController(TryOnService tryOnService) {
        this.tryOnService = tryOnService;
    }

    /**
     * Create a new try-on session from clothing item
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TryOnSessionResponse>> createTryOnSession(
            @Valid @RequestBody CreateTryOnSessionRequest request) {
        TryOnSessionResponse response = tryOnService.createTryOnSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Try-on session created successfully", response));
    }

    /**
     * Create a new try-on session directly from wardrobe item
     */
    @PostMapping("/from-wardrobe")
    public ResponseEntity<ApiResponse<TryOnSessionResponse>> createTryOnFromWardrobe(
            @Valid @RequestBody TryOnFromWardrobeRequest request) {
        TryOnSessionResponse response = tryOnService.createTryOnFromWardrobe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Try-on session created from wardrobe successfully", response));
    }

    /**
     * Get try-on session by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TryOnSessionResponse>> getTryOnSession(@PathVariable Long id) {
        TryOnSessionResponse response = tryOnService.getTryOnSessionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all try-on sessions for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TryOnSessionResponse>>> getTryOnSessionsByUserId(
            @PathVariable Long userId) {
        List<TryOnSessionResponse> responses = tryOnService.getTryOnSessionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get favorite try-on results for a user
     */
    @GetMapping("/user/{userId}/favorites")
    public ResponseEntity<ApiResponse<List<TryOnResultResponse>>> getFavoriteTryOnResults(
            @PathVariable Long userId) {
        List<TryOnResultResponse> responses = tryOnService.getFavoriteTryOnResults(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Generate try-on result for a session
     */
    @PostMapping("/{id}/generate")
    public ResponseEntity<ApiResponse<TryOnResult>> generateTryOnResult(@PathVariable Long id) {
        TryOnResult result = tryOnService.generateTryOnResult(id);
        return ResponseEntity.ok(ApiResponse.success("Try-on result generated successfully", result));
    }

    /**
     * Get try-on result for a session
     */
    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<TryOnResult>> getTryOnResult(@PathVariable Long id) {
        TryOnResult result = tryOnService.getTryOnResult(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Get detailed try-on result for a session
     */
    @GetMapping("/{id}/result/detail")
    public ResponseEntity<ApiResponse<TryOnResultResponse>> getTryOnResultDetail(@PathVariable Long id) {
        TryOnResultResponse response = tryOnService.getTryOnResultResponse(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update try-on session parameters (size, fit, avatar)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TryOnSessionResponse>> updateTryOnSession(
            @PathVariable Long id,
            @RequestBody UpdateTryOnSessionRequest request) {
        TryOnSessionResponse response = tryOnService.updateTryOnSession(id, request);
        return ResponseEntity.ok(ApiResponse.success("Try-on session updated successfully", response));
    }

    /**
     * Regenerate try-on result with updated parameters
     */
    @PostMapping("/{id}/regenerate")
    public ResponseEntity<ApiResponse<TryOnResult>> regenerateTryOnResult(@PathVariable Long id) {
        TryOnResult result = tryOnService.regenerateTryOnResult(id);
        return ResponseEntity.ok(ApiResponse.success("Try-on result regenerated successfully", result));
    }

    /**
     * Delete a try-on session
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTryOnSession(@PathVariable Long id) {
        tryOnService.deleteTryOnSession(id);
        return ResponseEntity.ok(ApiResponse.success("Try-on session deleted successfully", null));
    }

    /**
     * Toggle favorite status for a try-on result
     */
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<TryOnResultResponse>> toggleFavorite(@PathVariable Long id) {
        TryOnResultResponse response = tryOnService.toggleFavorite(id);
        return ResponseEntity.ok(ApiResponse.success("Favorite toggled successfully", response));
    }
}

