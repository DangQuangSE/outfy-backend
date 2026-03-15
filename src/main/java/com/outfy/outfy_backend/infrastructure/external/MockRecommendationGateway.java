package com.outfy.outfy_backend.infrastructure.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MockRecommendationGateway implements RecommendationGateway {

    private static final Logger logger = LoggerFactory.getLogger(MockRecommendationGateway.class);

    @Override
    public List<Map<String, Object>> recommend(Long userId, Long bodyProfileId, String occasion, List<Long> clothingItemIds) {
        logger.info("Mock recommendation for userId: {}, bodyProfileId: {}, occasion: {}, clothingItems: {}",
                   userId, bodyProfileId, occasion, clothingItemIds);

        // Mock implementation - return sample recommendations
        List<Map<String, Object>> results = new ArrayList<>();

        // Generate mock recommendations based on clothing items
        for (int i = 0; i < Math.min(clothingItemIds.size(), 5); i++) {
            Map<String, Object> result = new HashMap<>();
            result.put("clothingItemId", clothingItemIds.get(i));
            result.put("matchScore", 0.85 - (i * 0.1));
            result.put("reason", "Phù hợp với dịp " + occasion + " và phong cách của bạn");
            results.add(result);
        }

        return results;
    }
}

