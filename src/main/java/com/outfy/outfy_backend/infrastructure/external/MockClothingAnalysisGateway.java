package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MockClothingAnalysisGateway implements ClothingAnalysisGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(MockClothingAnalysisGateway.class);

    @Override
    public ClothingAnalysisResult analyze(Long clothingItemId) {
        logger.info("Mock clothing analysis for item id: {}", clothingItemId);
        
        // Mock implementation
        String garmentCategory = "Hoodie";
        String templateCode = "hoodie_template_v1";
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("color", "Black");
        attributes.put("sleeveType", "Long");
        attributes.put("fitType", "Loose");
        attributes.put("hasHood", true);
        attributes.put("hasZipper", true);
        
        Map<String, Object> garmentParameters = new HashMap<>();
        garmentParameters.put("chestWidth", 52);
        garmentParameters.put("bodyLength", 70);
        garmentParameters.put("sleeveLength", 63);
        
        String previewUrl = "/mock/clothing/" + templateCode + ".png";
        
        return new ClothingAnalysisResult(garmentCategory, templateCode, attributes, garmentParameters, previewUrl);
    }
}

