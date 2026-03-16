package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockTryOnGateway implements TryOnGateway {

    private static final Logger logger = LoggerFactory.getLogger(MockTryOnGateway.class);

    // GLB garment templates from static resources - matching actual file names
    private static final Map<String, String> GARMENT_TEMPLATES = new ConcurrentHashMap<>();
    static {
        // Map category to template file name (without .glb extension for URL)
        GARMENT_TEMPLATES.put("HOODIE", "/models/cloth/hoodie_template");
        GARMENT_TEMPLATES.put("T-SHIRT", "/models/cloth/tshirt_template");
        GARMENT_TEMPLATES.put("T_SHIRT", "/models/cloth/tshirt_template");
        GARMENT_TEMPLATES.put("FEMALE_TSHIRT", "/models/cloth/female_tshirt_template");
        GARMENT_TEMPLATES.put("SHIRT", "/models/cloth/shirt_template");
        GARMENT_TEMPLATES.put("JACKET", "/models/cloth/jacket_template");
        GARMENT_TEMPLATES.put("PANTS", "/models/cloth/pants_template");
        GARMENT_TEMPLATES.put("SHORTS", "/models/cloth/shorts_template");
        GARMENT_TEMPLATES.put("DRESS", "/models/cloth/dress_template");
        GARMENT_TEMPLATES.put("SKIRT", "/models/cloth/skirt_template");
        GARMENT_TEMPLATES.put("SHORT_SKIRT", "/models/cloth/short_skirt_template");
        GARMENT_TEMPLATES.put("CROP_TOP", "/models/cloth/crop_top_template");
    }

    // Body model templates from static resources - matching actual file names
    private static final Map<String, String> BODY_TEMPLATES = new ConcurrentHashMap<>();
    static {
        // Map avatar type to body model file name
        BODY_TEMPLATES.put("slim_male", "/models/body/slim_male");
        BODY_TEMPLATES.put("slim_female", "/models/body/slim_female");
        BODY_TEMPLATES.put("regular_male", "/models/body/regular_male");
        BODY_TEMPLATES.put("regular_female", "/models/body/regular_female");
        BODY_TEMPLATES.put("athletic_male", "/models/body/broad_male");  // using broad_male as athletic
        BODY_TEMPLATES.put("curvy_female", "/models/body/curvy_female");
        // Default fallbacks
        BODY_TEMPLATES.put("slim", "/models/body/slim_male");
        BODY_TEMPLATES.put("regular", "/models/body/regular_male");
        BODY_TEMPLATES.put("athletic", "/models/body/broad_male");
        BODY_TEMPLATES.put("plus", "/models/body/curvy_female");
    }

    // Avatar presets with body type description
    private static final Map<String, String> AVATAR_PRESETS = new ConcurrentHashMap<>();
    static {
        AVATAR_PRESETS.put("slim_male", "Slim male body type");
        AVATAR_PRESETS.put("slim_female", "Slim female body type");
        AVATAR_PRESETS.put("regular_male", "Regular male body type");
        AVATAR_PRESETS.put("regular_female", "Regular female body type");
        AVATAR_PRESETS.put("athletic_male", "Athletic/broad male body type");
        AVATAR_PRESETS.put("curvy_female", "Curvy female body type");
        AVATAR_PRESETS.put("slim", "Slim body type - lean build");
        AVATAR_PRESETS.put("regular", "Regular body type - average build");
        AVATAR_PRESETS.put("athletic", "Athletic body type - muscular build");
        AVATAR_PRESETS.put("plus", "Plus body type - fuller build");
    }

    // Try-on model path: /models/try-on/body_{bodyType}_cloth_{category}.glb
    private static final String TRYON_MODEL_PATH = "/models/try-on";

    private final Random random = new Random();

    // Store clothing item category mapping for mock (in real app, this would come from database)
    private final Map<Long, String> clothingCategoryCache = new ConcurrentHashMap<>();
    private final Map<Long, String> clothingColorCache = new ConcurrentHashMap<>();

    public MockTryOnGateway() {
        // Initialize mock clothing data
        initMockClothingData();
    }

    private void initMockClothingData() {
        // Mock some clothing data for demo - mapping to actual template names
        clothingCategoryCache.put(1L, "HOODIE");
        clothingCategoryCache.put(2L, "T-SHIRT");
        clothingCategoryCache.put(3L, "JACKET");
        clothingCategoryCache.put(4L, "PANTS");
        clothingCategoryCache.put(5L, "DRESS");
        clothingCategoryCache.put(6L, "SHIRT");
        clothingCategoryCache.put(7L, "SKIRT");
        clothingCategoryCache.put(8L, "SHORTS");
        clothingCategoryCache.put(9L, "CROP_TOP");
        clothingCategoryCache.put(10L, "SHORT_SKIRT");

        clothingColorCache.put(1L, "blue");
        clothingColorCache.put(2L, "white");
        clothingColorCache.put(3L, "black");
        clothingColorCache.put(4L, "gray");
        clothingColorCache.put(5L, "red");
        clothingColorCache.put(6L, "green");
        clothingColorCache.put(7L, "pink");
    }

    @Override
    public TryOnResult generate(String avatarId, Long clothingItemId, String size, String fitType) {
        logger.info("Mock try-on generation for avatarId: {}, clothingItemId: {}, size: {}, fitType: {}",
                avatarId, clothingItemId, size, fitType);

        // Normalize avatar ID to match body template naming
        String normalizedAvatarId = normalizeAvatarId(avatarId);

        // Get clothing category from cache (or default)
        String category = clothingCategoryCache.getOrDefault(clothingItemId, "T-SHIRT");
        String garmentColor = clothingColorCache.getOrDefault(clothingItemId, "gray");

        // Get garment model URL (without extension - user will add .glb or .png as needed)
        String garmentModelUrl = GARMENT_TEMPLATES.getOrDefault(category, "/models/cloth/tshirt_template");

        // Get body model URL
        String bodyModelUrl = BODY_TEMPLATES.getOrDefault(normalizedAvatarId, "/models/body/regular_male");

        // Generate fit score based on avatar and fit type
        Double fitScore = calculateFitScore(normalizedAvatarId, fitType);

        // Generate preview URL - matching user's naming convention
        // User will create: {bodyType}_{garmentCategory}.png in /models/tryon/
        String previewUrl = generatePreviewUrl(normalizedAvatarId, category);

        // Generate note
        String note = generateNote(normalizedAvatarId, category, fitType, fitScore);

        // Applied parameters
        Map<String, Object> appliedParams = new HashMap<>();
        appliedParams.put("scale", calculateScale(normalizedAvatarId));
        appliedParams.put("offsetX", 0);
        appliedParams.put("offsetY", 0);
        appliedParams.put("size", size != null ? size : "M");
        appliedParams.put("fitType", fitType != null ? fitType : "regular");
        appliedParams.put("garmentModelUrl", garmentModelUrl + ".glb");
        appliedParams.put("garmentCategory", category);
        appliedParams.put("garmentColor", garmentColor);
        appliedParams.put("bodyModelUrl", bodyModelUrl + ".glb");
        appliedParams.put("bodyType", normalizedAvatarId);

        TryOnResult result = new TryOnResult();
        result.setPreviewUrl(previewUrl);
        result.setFitScore(fitScore);
        result.setNote(note);
        result.setAppliedParams(appliedParams);

        logger.info("Generated mock try-on result: previewUrl={}, fitScore={}", previewUrl, fitScore);

        return result;
    }

    /**
     * Normalize avatar ID to match body template naming convention
     * e.g., "slim" -> "slim_male", "regular" -> "regular_female"
     */
    private String normalizeAvatarId(String avatarId) {
        if (avatarId == null) {
            return "regular_male";
        }

        // Already contains underscore (e.g., "slim_male")
        if (avatarId.contains("_")) {
            return avatarId.toLowerCase();
        }

        // Map short names to full names with gender - default to male
        switch (avatarId.toLowerCase()) {
            case "slim":
                return "slim_male";
            case "regular":
                return "regular_male";
            case "athletic":
                return "athletic_male";
            case "plus":
                return "curvy_female";
            default:
                return "regular_male";
        }
    }

    private Double calculateFitScore(String avatarId, String fitType) {
        double baseScore = 0.75 + (random.nextDouble() * 0.20); // 0.75 - 0.95

        // Adjust based on fit type
        if (fitType != null) {
            switch (fitType.toLowerCase()) {
                case "slim":
                    if (avatarId.contains("slim") || avatarId.contains("athletic")) {
                        baseScore += 0.05;
                    } else if (avatarId.contains("plus") || avatarId.contains("curvy")) {
                        baseScore -= 0.08;
                    }
                    break;
                case "loose":
                case "oversize":
                    if (avatarId.contains("plus") || avatarId.contains("curvy")) {
                        baseScore += 0.05;
                    } else if (avatarId.contains("slim")) {
                        baseScore -= 0.05;
                    }
                    break;
                case "regular":
                case "normal":
                    // Regular fit works well for all
                    break;
            }
        }

        return Math.round(baseScore * 100.0) / 100.0;
    }

    private String generatePreviewUrl(String avatarId, String category) {
        // Generate try-on model URL matching user's file naming convention
        // User creates: /models/try-on/body_{bodyType}_cloth_{category}.glb
        // e.g., /models/try-on/body_slim_male_cloth_hoodie.glb
        // e.g., /models/try-on/body_curvy_female_cloth_dress.glb

        String bodyType = avatarId.toLowerCase();
        String garmentType = category.toLowerCase().replace("-", "_");

        return String.format("%s/body_%s_cloth_%s.glb", TRYON_MODEL_PATH, bodyType, garmentType);
    }

    private String generateNote(String avatarId, String category, String fitType, Double fitScore) {
        String avatarDesc = AVATAR_PRESETS.getOrDefault(avatarId, "custom avatar");
        String fitDesc = fitType != null ? fitType : "regular";

        if (fitScore >= 0.9) {
            return String.format("Excellent fit! The %s looks perfect on %s with %s fit.",
                    category.toLowerCase().replace("-", " "), avatarDesc, fitDesc);
        } else if (fitScore >= 0.8) {
            return String.format("Good fit! The %s fits well on %s.",
                    category.toLowerCase().replace("-", " "), avatarDesc);
        } else if (fitScore >= 0.7) {
            return String.format("The %s has a %s fit on %s. Consider trying a different size.",
                    category.toLowerCase().replace("-", " "), fitDesc, avatarDesc);
        } else {
            return String.format("The %s may not be the best fit for %s. Try a different style or size.",
                    category.toLowerCase().replace("-", " "), avatarDesc);
        }
    }

    private double calculateScale(String avatarId) {
        switch (avatarId != null ? avatarId.toLowerCase() : "regular_male") {
            case "slim_male":
            case "slim_female":
                return 0.95;
            case "athletic_male":
            case "broad_male":
                return 1.05;
            case "curvy_female":
                return 1.15;
            case "regular_male":
            case "regular_female":
            default:
                return 1.0;
        }
    }

    /**
     * Get all available body type templates
     */
    public Map<String, String> getAvailableBodyTypes() {
        return new HashMap<>(BODY_TEMPLATES);
    }

    /**
     * Get all available garment templates
     */
    public Map<String, String> getAvailableGarments() {
        return new HashMap<>(GARMENT_TEMPLATES);
    }
}

