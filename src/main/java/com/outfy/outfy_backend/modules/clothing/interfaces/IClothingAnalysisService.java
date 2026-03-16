package com.outfy.outfy_backend.modules.clothing.interfaces;

import com.outfy.outfy_backend.modules.clothing.dto.request.AnalyzeClothingRequest;
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
     * Analyze clothing item from database
     */
    ClothingAnalysisResult analyzeClothing(Long clothingId);

    /**
     * Get analysis result for clothing item
     */
    ClothingAnalysisResult getAnalysisResult(Long clothingId);

    /**
     * Analyze clothing directly from image (for demo without database)
     */
    ClothingAnalysisResult analyzeClothingDirect(AnalyzeClothingRequest request);
}

