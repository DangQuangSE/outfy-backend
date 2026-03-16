package com.outfy.outfy_backend.modules.clothing.interfaces;

import com.outfy.outfy_backend.modules.clothing.dto.request.AnalyzeClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.request.ConfirmClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.request.CreateClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingItemResponse;

import java.util.List;

/**
 * Interface for Clothing Analysis service operations
 */
public interface IClothingAnalysisService {

    /**
     * Create a new clothing item
     */
    ClothingItemResponse createClothingItem(CreateClothingRequest request);

    /**
     * Get clothing item by ID
     */
    ClothingItemResponse getClothingItemById(Long id);

    /**
     * Get all clothing items for a user
     */
    List<ClothingItemResponse> getClothingItemsByUserId(Long userId);

    /**
     * Get wardrobe items (only CONFIRMED items)
     */
    List<ClothingItemResponse> getWardrobeItems(Long userId);

    /**
     * Analyze clothing item from database
     */
    ClothingAnalysisResult analyzeClothing(Long clothingId);

    /**
     * Get analysis result for clothing item
     */
    ClothingAnalysisResult getAnalysisResult(Long clothingId);

    /**
     * Confirm clothing item to add to wardrobe
     */
    ClothingItemResponse confirmClothingItem(Long clothingId, ConfirmClothingRequest request);

    /**
     * Delete a clothing item
     */
    void deleteClothingItem(Long id, Long userId);

    /**
     * Re-analyze a clothing item
     */
    ClothingAnalysisResult reAnalyzeClothing(Long clothingId);

    /**
     * Analyze clothing directly from image (for demo without database)
     * Returns ClothingAnalysisResult with clothingItemId
     */
    ClothingAnalysisResult analyzeClothingDirect(AnalyzeClothingRequest request);
}

