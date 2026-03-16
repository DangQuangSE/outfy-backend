package com.outfy.outfy_backend.modules.clothing.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.clothing.dto.request.AnalyzeClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.request.ConfirmClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.request.CreateClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingItemResponse;
import com.outfy.outfy_backend.modules.clothing.interfaces.IClothingAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/clothes")
public class ClothingController {

    private final IClothingAnalysisService clothingService;

    public ClothingController(IClothingAnalysisService clothingService) {
        this.clothingService = clothingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClothingItemResponse>> createClothingItem(
            @Valid @RequestBody CreateClothingRequest request) {
        ClothingItemResponse response = clothingService.createClothingItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Clothing item created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClothingItemResponse>> getClothingItem(@PathVariable Long id) {
        ClothingItemResponse response = clothingService.getClothingItemById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ClothingItemResponse>>> getClothingItemsByUserId(
            @PathVariable Long userId) {
        List<ClothingItemResponse> responses = clothingService.getClothingItemsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get wardrobe items (only CONFIRMED items)
     */
    @GetMapping("/user/{userId}/wardrobe")
    public ResponseEntity<ApiResponse<List<ClothingItemResponse>>> getWardrobeItems(
            @PathVariable Long userId) {
        List<ClothingItemResponse> responses = clothingService.getWardrobeItems(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<ApiResponse<ClothingAnalysisResult>> analyzeClothing(@PathVariable Long id) {
        ClothingAnalysisResult result = clothingService.analyzeClothing(id);
        return ResponseEntity.ok(ApiResponse.success("Clothing analyzed successfully", result));
    }

    @PostMapping("/{id}/reanalyze")
    public ResponseEntity<ApiResponse<ClothingAnalysisResult>> reAnalyzeClothing(@PathVariable Long id) {
        ClothingAnalysisResult result = clothingService.reAnalyzeClothing(id);
        return ResponseEntity.ok(ApiResponse.success("Clothing re-analyzed successfully", result));
    }

    @GetMapping("/{id}/analysis")
    public ResponseEntity<ApiResponse<ClothingAnalysisResult>> getAnalysisResult(@PathVariable Long id) {
        ClothingAnalysisResult result = clothingService.getAnalysisResult(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Confirm clothing item to add to wardrobe
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<ClothingItemResponse>> confirmClothingItem(
            @PathVariable Long id,
            @RequestBody ConfirmClothingRequest request) {
        ClothingItemResponse response = clothingService.confirmClothingItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Clothing item confirmed and added to wardrobe", response));
    }

    /**
     * Delete a clothing item (only DRAFT or FAILED status)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClothingItem(
            @PathVariable Long id,
            @RequestParam Long userId) {
        clothingService.deleteClothingItem(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Clothing item deleted successfully", null));
    }

    /**
     * Analyze clothing directly from image URL (for demo without database)
     * Returns ClothingAnalysisResult with clothingItemId to add to wardrobe later
     */
    @PostMapping("/analyze-direct")
    public ResponseEntity<ApiResponse<ClothingAnalysisResult>> analyzeClothingDirect(
            @Valid @RequestBody AnalyzeClothingRequest request) {
        ClothingAnalysisResult result = clothingService.analyzeClothingDirect(request);
        return ResponseEntity.ok(ApiResponse.success("Clothing analyzed successfully", result));
    }
}

