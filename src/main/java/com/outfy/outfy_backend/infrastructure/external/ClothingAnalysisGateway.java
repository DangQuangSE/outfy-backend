package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;

public interface ClothingAnalysisGateway {
    ClothingAnalysisResult analyze(Long clothingItemId);
}

