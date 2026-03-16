package com.outfy.outfy_backend.modules.wardrobe.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.wardrobe.dto.request.CreateWardrobeItemRequest;
import com.outfy.outfy_backend.modules.wardrobe.dto.response.WardrobeItemResponse;
import com.outfy.outfy_backend.modules.wardrobe.service.WardrobeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/wardrobe")
public class WardrobeController {

    private final WardrobeService wardrobeService;

    public WardrobeController(WardrobeService wardrobeService) {
        this.wardrobeService = wardrobeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WardrobeItemResponse>> createWardrobeItem(
            @Valid @RequestBody CreateWardrobeItemRequest request) {
        WardrobeItemResponse response = wardrobeService.createWardrobeItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wardrobe item created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WardrobeItemResponse>> getWardrobeItem(@PathVariable Long id) {
        WardrobeItemResponse response = wardrobeService.getWardrobeItemById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<WardrobeItemResponse>>> getWardrobeItemsByUserId(
            @PathVariable Long userId) {
        List<WardrobeItemResponse> responses = wardrobeService.getWardrobeItemsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<ApiResponse<List<WardrobeItemResponse>>> getWardrobeItemsByCategory(
            @PathVariable Long userId, @PathVariable String category) {
        List<WardrobeItemResponse> responses = wardrobeService.getWardrobeItemsByCategory(userId, category);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}/favorites")
    public ResponseEntity<ApiResponse<List<WardrobeItemResponse>>> getFavoriteItems(@PathVariable Long userId) {
        List<WardrobeItemResponse> responses = wardrobeService.getFavoriteItems(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}/season/{season}")
    public ResponseEntity<ApiResponse<List<WardrobeItemResponse>>> getWardrobeItemsBySeason(
            @PathVariable Long userId, @PathVariable String season) {
        List<WardrobeItemResponse> responses = wardrobeService.getWardrobeItemsBySeason(userId, season);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<ApiResponse<WardrobeItemResponse>> toggleFavorite(@PathVariable Long id) {
        WardrobeItemResponse response = wardrobeService.toggleFavorite(id);
        return ResponseEntity.ok(ApiResponse.success("Favorite toggled successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WardrobeItemResponse>> updateWardrobeItem(
            @PathVariable Long id,
            @Valid @RequestBody CreateWardrobeItemRequest request) {
        WardrobeItemResponse response = wardrobeService.updateWardrobeItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Wardrobe item updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWardrobeItem(@PathVariable Long id) {
        wardrobeService.deleteWardrobeItem(id);
        return ResponseEntity.ok(ApiResponse.success("Wardrobe item deleted successfully", null));
    }

    /**
     * Add analyzed clothing item to wardrobe
     */
    @PostMapping("/from-clothing")
    public ResponseEntity<ApiResponse<WardrobeItemResponse>> addFromClothing(
            @RequestParam Long clothingItemId,
            @RequestParam Long userId,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) String notes) {
        WardrobeItemResponse response = wardrobeService.addToWardrobe(clothingItemId, userId, season, notes);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Added to wardrobe successfully", response));
    }
}

