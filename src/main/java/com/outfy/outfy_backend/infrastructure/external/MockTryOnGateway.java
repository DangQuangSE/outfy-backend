package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MockTryOnGateway implements TryOnGateway {

    private static final Logger logger = LoggerFactory.getLogger(MockTryOnGateway.class);

    @Override
    public TryOnResult generate(Long bodyProfileId, Long clothingItemId) {
        logger.info("Mock try-on generation for bodyProfileId: {}, clothingItemId: {}", bodyProfileId, clothingItemId);

        // Mock implementation
        String previewUrl = "/mock/tryon/result_" + bodyProfileId + "_" + clothingItemId + ".png";
        Double fitScore = 0.82;
        String note = "Garment fits regular body preset.";

        Map<String, Object> appliedParams = new HashMap<>();
        appliedParams.put("scale", 1.0);
        appliedParams.put("offsetX", 0);
        appliedParams.put("offsetY", 0);

        TryOnResult result = new TryOnResult();
        result.setPreviewUrl(previewUrl);
        result.setFitScore(fitScore);
        result.setNote(note);
        result.setAppliedParams(appliedParams);

        return result;
    }
}

