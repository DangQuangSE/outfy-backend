package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MockBodyGenerationGateway implements IBodyGenerationGateway {
    
    private static final Logger logger = LoggerFactory.getLogger(MockBodyGenerationGateway.class);

    // Model URL mapping - using local GLB files
    private static final Map<String, String> MODEL_URL_MAP = Map.of(
            "slim_male", "/models/body/slim_male.glb",
            "regular_male", "/models/body/regular_male.glb",
            "broad_male", "/models/body/broad_male.glb",
            "slim_female", "/models/body/slim_female.glb",
            "regular_female", "/models/body/regular_female.glb",
            "curvy_female", "/models/body/curvy_female.glb"
    );

    @Override
    public BodyGenerationResult generate(Long bodyProfileId) {
        logger.info("Mock body generation for profile id: {}", bodyProfileId);
        
        // Simplified version - returns default values
        String bodyType = "Regular";
        String avatarPresetCode = "regular_01";
        String modelUrl = "/models/body/regular_female.glb";
        
        Map<String, Double> shapeParams = createDefaultShapeParams();
        
        return new BodyGenerationResult(bodyType, avatarPresetCode, shapeParams, modelUrl, modelUrl, 0.85);
    }

    /**
     * Generate avatar from measurements (new method for direct API)
     * This follows the rule-based logic from the document
     */
    public BodyGenerationResult generateFromMeasurements(
            String gender, 
            double heightCm, double weightKg,
            double chestCm, double waistCm, double hipCm, 
            double shoulderCm, double inseamCm) {
        
        logger.info("Generating avatar from measurements - gender: {}, height: {}, weight: {}", 
                gender, heightCm, weightKg);
        
        // Step 1: Calculate BMI
        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);
        
        // Step 2: Calculate derived metrics
        double waistToHipRatio = waistCm / hipCm;
        
        // Step 3: Determine body type
        String bodyType = determineBodyType(gender, bmi, waistToHipRatio, shoulderCm);
        
        // Step 4: Determine avatar preset code
        String avatarPresetCode = determineAvatarPreset(gender, bodyType);
        
        // Step 5: Get model URL
        String modelKey = (bodyType + "_" + gender).toLowerCase();
        String modelUrl = MODEL_URL_MAP.getOrDefault(modelKey, "/models/body/regular_female.glb");
        
        // Step 6: Generate shape parameters
        Map<String, Double> shapeParams = generateShapeParams(
                heightCm, chestCm, waistCm, hipCm, shoulderCm, inseamCm, bodyType);
        
        // Step 7: Calculate confidence
        double confidence = calculateConfidence(bmi, waistToHipRatio);
        
        logger.info("Generated avatar - bodyType: {}, preset: {}, modelUrl: {}", 
                bodyType, avatarPresetCode, modelUrl);
        
        return new BodyGenerationResult(bodyType, avatarPresetCode, shapeParams, modelUrl, modelUrl, confidence);
    }

    /**
     * Determine body type based on BMI and ratios
     */
    private String determineBodyType(String gender, double bmi, double waistToHipRatio, double shoulderCm) {
        boolean isFemale = "female".equalsIgnoreCase(gender);
        
        if (isFemale) {
            if (bmi < 18.5) return "Slim";
            else if (bmi < 24) {
                return waistToHipRatio > 0.75 ? "Curvy" : "Regular";
            } else if (bmi < 28) return "Curvy";
            else return "Curvy";
        } else {
            if (bmi < 18.5) return "Slim";
            else if (bmi < 25) {
                return shoulderCm > 45 ? "Broad" : "Regular";
            } else if (bmi < 30) return "Broad";
            else return "Broad";
        }
    }

    /**
     * Determine avatar preset code
     */
    private String determineAvatarPreset(String gender, String bodyType) {
        return gender.toLowerCase() + "_" + bodyType.toLowerCase() + "_01";
    }

    /**
     * Generate shape parameters based on measurements
     */
    private Map<String, Double> generateShapeParams(
            double heightCm, double chestCm, double waistCm, 
            double hipCm, double shoulderCm, double inseamCm, String bodyType) {
        
        Map<String, Double> params = new HashMap<>();
        
        double heightScale = 1.0 + (heightCm - 170) / 170 * 0.1;
        double shoulderScale = 1.0 + (shoulderCm - 40) / 40 * 0.1;
        double chestScale = 1.0 + (chestCm - 90) / 90 * 0.1;
        double waistScale = 1.0 + (waistCm - 75) / 75 * 0.1;
        double hipScale = 1.0 + (hipCm - 95) / 95 * 0.1;
        double legScale = 1.0 + (inseamCm - 80) / 80 * 0.1;
        
        switch (bodyType.toLowerCase()) {
            case "slim":
                waistScale *= 0.9;
                hipScale *= 0.95;
                chestScale *= 0.95;
                break;
            case "curvy":
                waistScale *= 1.1;
                hipScale *= 1.1;
                break;
            case "broad":
                shoulderScale *= 1.1;
                chestScale *= 1.05;
                break;
        }
        
        params.put("heightScale", Math.round(heightScale * 100.0) / 100.0);
        params.put("shoulderScale", Math.round(shoulderScale * 100.0) / 100.0);
        params.put("chestScale", Math.round(chestScale * 100.0) / 100.0);
        params.put("waistScale", Math.round(waistScale * 100.0) / 100.0);
        params.put("hipScale", Math.round(hipScale * 100.0) / 100.0);
        params.put("legScale", Math.round(legScale * 100.0) / 100.0);
        
        return params;
    }

    /**
     * Calculate confidence based on data quality
     */
    private double calculateConfidence(double bmi, double waistToHipRatio) {
        double baseConfidence = 0.7;
        if (bmi >= 18.5 && bmi < 30) baseConfidence += 0.1;
        if (waistToHipRatio > 0) baseConfidence += 0.1;
        return Math.min(baseConfidence, 0.95);
    }

    private Map<String, Double> createDefaultShapeParams() {
        Map<String, Double> params = new HashMap<>();
        params.put("heightScale", 1.0);
        params.put("shoulderScale", 1.0);
        params.put("chestScale", 1.0);
        params.put("waistScale", 1.0);
        params.put("hipScale", 1.0);
        params.put("legScale", 1.0);
        return params;
    }
}

