package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;
import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MockBodyGenerationGateway implements BodyGenerationGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(MockBodyGenerationGateway.class);

    @Override
    public BodyGenerationResult generate(Long bodyProfileId) {
        logger.info("Mock body generation for profile id: {}", bodyProfileId);
        
        // Mock implementation - calculate based on BMI
        String bodyType = "Regular";
        String avatarPresetCode = "regular_01";
        
        Map<String, Double> shapeParams = new HashMap<>();
        shapeParams.put("heightScale", 1.0);
        shapeParams.put("shoulderScale", 1.0);
        shapeParams.put("chestScale", 1.0);
        shapeParams.put("waistScale", 1.0);
        shapeParams.put("hipScale", 1.0);
        shapeParams.put("legScale", 1.0);
        
        String previewUrl = "/mock/avatar/" + avatarPresetCode + ".png";
        
        return new BodyGenerationResult(bodyType, avatarPresetCode, shapeParams, previewUrl, 0.85);
    }
}

