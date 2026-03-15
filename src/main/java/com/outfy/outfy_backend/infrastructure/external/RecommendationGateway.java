package com.outfy.outfy_backend.infrastructure.external;

import java.util.List;
import java.util.Map;

public interface RecommendationGateway {
    List<Map<String, Object>> recommend(Long userId, Long bodyProfileId, String occasion, List<Long> clothingItemIds);
}

