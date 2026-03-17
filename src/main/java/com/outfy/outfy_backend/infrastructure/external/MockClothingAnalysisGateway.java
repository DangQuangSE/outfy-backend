package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingAnalysisResult;
import com.outfy.outfy_backend.modules.clothing.enums.FitType;
import com.outfy.outfy_backend.modules.clothing.enums.GarmentCategory;
import com.outfy.outfy_backend.modules.clothing.enums.SleeveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MockClothingAnalysisGateway implements ClothingAnalysisGateway {

    private static final Logger logger = LoggerFactory.getLogger(MockClothingAnalysisGateway.class);

    // Template URL mapping - matching actual GLB file names
    private static final Map<GarmentCategory, String> TEMPLATE_URL_MAP = Map.ofEntries(
            Map.entry(GarmentCategory.TSHIRT, "/models/cloth/tshirt_template.glb"),
            Map.entry(GarmentCategory.HOODIE, "/models/cloth/hoodie_template.glb"),
            Map.entry(GarmentCategory.SHIRT, "/models/cloth/shirt_template.glb"),
            Map.entry(GarmentCategory.PANTS, "/models/cloth/pants_template.glb"),
            Map.entry(GarmentCategory.SHORTS, "/models/cloth/shorts_template.glb"),
            Map.entry(GarmentCategory.SKIRT, "/models/cloth/skirt_template.glb"),
            Map.entry(GarmentCategory.SHORT_SKIRT, "/models/cloth/short_skirt_template.glb"),
            Map.entry(GarmentCategory.DRESS, "/models/cloth/dress_template.glb"),
            Map.entry(GarmentCategory.JACKET, "/models/cloth/jacket_template.glb"),
            Map.entry(GarmentCategory.CLOTH_TOP, "/models/cloth/cloth_top_template.glb"),
            Map.entry(GarmentCategory.FEMALE_SHIRT, "/models/cloth/female_shirt_template.glb")
    );

    // Default attributes by category
    private static final Map<GarmentCategory, Map<String, Object>> DEFAULT_ATTRIBUTES = Map.ofEntries(
            Map.entry(GarmentCategory.HOODIE, Map.of(
                    "color", "Black",
                    "sleeveType", "Long",
                    "fitType", "Loose",
                    "hasHood", true,
                    "hasZipper", true
            )),
            Map.entry(GarmentCategory.TSHIRT, Map.of(
                    "color", "White",
                    "sleeveType", "Short",
                    "fitType", "Regular",
                    "hasHood", false,
                    "hasZipper", false
            )),
            Map.entry(GarmentCategory.SHIRT, Map.of(
                    "color", "Blue",
                    "sleeveType", "Short",
                    "fitType", "Regular",
                    "hasCollar", true,
                    "hasButton", true
            )),
            Map.entry(GarmentCategory.PANTS, Map.of(
                    "color", "Blue",
                    "fitType", "Regular",
                    "waistType", "Regular",
                    "length", "Full"
            )),
            Map.entry(GarmentCategory.SKIRT, Map.of(
                    "color", "Black",
                    "fitType", "Regular",
                    "length", "Mini"
            )),
            Map.entry(GarmentCategory.DRESS, Map.of(
                    "color", "Red",
                    "sleeveType", "Long",
                    "fitType", "Regular",
                    "length", "Midi"
            )),
            Map.entry(GarmentCategory.JACKET, Map.of(
                    "color", "Brown",
                    "sleeveType", "Long",
                    "fitType", "Regular",
                    "hasZipper", true,
                    "hasHood", false
            )),
            Map.entry(GarmentCategory.SHORTS, Map.of(
                    "color", "Gray",
                    "fitType", "Regular",
                    "length", "Short"
            )),
            Map.entry(GarmentCategory.SHORT_SKIRT, Map.of(
                    "color", "Pink",
                    "fitType", "Regular",
                    "length", "Mini"
            )),
            Map.entry(GarmentCategory.CLOTH_TOP, Map.of(
                    "color", "White",
                    "sleeveType", "Short",
                    "fitType", "Slim",
                    "hasHood", false,
                    "hasZipper", false
            )),
            Map.entry(GarmentCategory.FEMALE_SHIRT, Map.of(
                    "color", "White",
                    "sleeveType", "Short",
                    "fitType", "Regular",
                    "hasCollar", true,
                    "hasButton", true
            ))
    );

    // Default garment parameters by category
    private static final Map<GarmentCategory, Map<String, Object>> DEFAULT_GARMENT_PARAMS = Map.ofEntries(
            Map.entry(GarmentCategory.HOODIE, Map.of(
                    "chestWidth", 52.0,
                    "bodyLength", 70.0,
                    "sleeveLength", 63.0
            )),
            Map.entry(GarmentCategory.TSHIRT, Map.of(
                    "chestWidth", 48.0,
                    "bodyLength", 68.0,
                    "sleeveLength", 22.0
            )),
            Map.entry(GarmentCategory.SHIRT, Map.of(
                    "chestWidth", 50.0,
                    "bodyLength", 72.0,
                    "sleeveLength", 60.0
            )),
            Map.entry(GarmentCategory.PANTS, Map.of(
                    "waistWidth", 38.0,
                    "inseamLength", 82.0,
                    "legWidth", 22.0
            )),
            Map.entry(GarmentCategory.SKIRT, Map.of(
                    "waistWidth", 32.0,
                    "length", 45.0,
                    "width", 52.0
            )),
            Map.entry(GarmentCategory.DRESS, Map.of(
                    "chestWidth", 42.0,
                    "waistWidth", 34.0,
                    "length", 110.0
            )),
            Map.entry(GarmentCategory.JACKET, Map.of(
                    "chestWidth", 54.0,
                    "bodyLength", 75.0,
                    "sleeveLength", 64.0
            )),
            Map.entry(GarmentCategory.SHORTS, Map.of(
                    "waistWidth", 34.0,
                    "inseamLength", 15.0,
                    "legWidth", 24.0
            )),
            Map.entry(GarmentCategory.SHORT_SKIRT, Map.of(
                    "waistWidth", 30.0,
                    "length", 35.0,
                    "width", 48.0
            )),
            Map.entry(GarmentCategory.CLOTH_TOP, Map.of(
                    "chestWidth", 40.0,
                    "bodyLength", 45.0,
                    "sleeveLength", 18.0
            )),
            Map.entry(GarmentCategory.FEMALE_SHIRT, Map.of(
                    "chestWidth", 42.0,
                    "bodyLength", 60.0,
                    "sleeveLength", 20.0
            ))
    );

    @Override
    public ClothingAnalysisResult analyze(Long clothingItemId) {
        logger.info("Mock clothing analysis for item id: {}", clothingItemId);
        return createDefaultResult();
    }

    /**
     * Analyze clothing directly from image URL (new method for demo)
     * This follows the rule-based logic from the document
     */
    @Override
    public ClothingAnalysisResult analyzeFromImage(String imageUrl, String fileName) {
        logger.info("Mock clothing analysis from image - url: {}, filename: {}", imageUrl, fileName);

        String filename = fileName != null ? fileName : extractFilenameFromUrl(imageUrl);

        // Step 1: Classify garment category from filename
        GarmentCategory category = GarmentCategory.fromFilename(filename);

        // Step 2: Determine sleeve type
        SleeveType sleeveType = SleeveType.fromFilename(filename, category);

        // Step 3: Determine fit type
        FitType fitType = FitType.fromFilename(filename);

        // Step 4: Get template code
        String templateCode = category.getTemplateCode();

        // Step 5: Get template URL
        String templateUrl = TEMPLATE_URL_MAP.getOrDefault(category, "/models/cloth/tshirt_template.glb");

        // Step 6: Get attributes
        Map<String, Object> attributes = new HashMap<>(DEFAULT_ATTRIBUTES.getOrDefault(category, new HashMap<>()));
        attributes.put("sleeveType", sleeveType.name());
        attributes.put("fitType", fitType.name());

        // Step 7: Get garment parameters
        Map<String, Object> garmentParameters = new HashMap<>(
                DEFAULT_GARMENT_PARAMS.getOrDefault(category, new HashMap<>())
        );

        // Step 8: Generate preview URL
        String previewUrl = templateUrl;

        // Step 9: Calculate confidence
        double confidence = calculateConfidence(category, filename);

        logger.info("Analyzed clothing - category: {}, template: {}, confidence: {}",
                category, templateCode, confidence);

        // Use templateUrl as modelUrl (GLB file path)
        return new ClothingAnalysisResult(
                null,
                category.name(),
                templateCode,
                attributes,
                garmentParameters,
                previewUrl,
                templateUrl,  // modelUrl - path to GLB file
                confidence
        );
    }

    /**
     * Analyze clothing with explicit garment category
     */
    @Override
    public ClothingAnalysisResult analyzeFromImageWithCategory(String imageUrl, String fileName, String garmentCategory) {
        logger.info("Mock clothing analysis with category - url: {}, filename: {}, category: {}",
                imageUrl, fileName, garmentCategory);

        // Parse category from string
        GarmentCategory category;
        try {
            category = GarmentCategory.valueOf(garmentCategory.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid garment category: {}, falling back to filename detection", garmentCategory);
            // Fallback to filename detection
            String filename = fileName != null ? fileName : extractFilenameFromUrl(imageUrl);
            category = GarmentCategory.fromFilename(filename);
        }

        // Determine sleeve type (use default based on category)
        SleeveType sleeveType = SleeveType.fromFilename(fileName, category);

        // Determine fit type
        FitType fitType = FitType.fromFilename(fileName);

        // Get template code
        String templateCode = category.getTemplateCode();

        // Get template URL
        String templateUrl = TEMPLATE_URL_MAP.getOrDefault(category, "/models/cloth/tshirt_template.glb");

        // Get attributes
        Map<String, Object> attributes = new HashMap<>(DEFAULT_ATTRIBUTES.getOrDefault(category, new HashMap<>()));
        attributes.put("sleeveType", sleeveType.name());
        attributes.put("fitType", fitType.name());

        // Get garment parameters
        Map<String, Object> garmentParameters = new HashMap<>(
                DEFAULT_GARMENT_PARAMS.getOrDefault(category, new HashMap<>())
        );

        // Generate preview URL
        String previewUrl = templateUrl;

        // Confidence is higher when category is explicitly provided
        double confidence = 0.95;

        logger.info("Analyzed clothing with explicit category - category: {}, template: {}, confidence: {}",
                category, templateCode, confidence);

        return new ClothingAnalysisResult(
                null,
                category.name(),
                templateCode,
                attributes,
                garmentParameters,
                previewUrl,
                templateUrl,
                confidence
        );
    }

    private ClothingAnalysisResult createDefaultResult() {
        GarmentCategory category = GarmentCategory.HOODIE;
        String templateUrl = TEMPLATE_URL_MAP.get(category);
        return new ClothingAnalysisResult(
                null,
                category.name(),
                category.getTemplateCode(),
                new HashMap<>(DEFAULT_ATTRIBUTES.get(category)),
                new HashMap<>(DEFAULT_GARMENT_PARAMS.get(category)),
                templateUrl,
                templateUrl,  // modelUrl - path to GLB file
                0.85
        );
    }

    private String extractFilenameFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return "";
        }
        try {
            String path = imageUrl;
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash >= 0) {
                path = path.substring(lastSlash + 1);
            }
            int queryIdx = path.indexOf('?');
            if (queryIdx > 0) {
                path = path.substring(0, queryIdx);
            }
            return path;
        } catch (Exception e) {
            logger.warn("Failed to extract filename from URL: {}", imageUrl);
            return "";
        }
    }

    private double calculateConfidence(GarmentCategory category, String filename) {
        double baseConfidence = 0.7;
        if (filename != null && !filename.isBlank()) {
            String lower = filename.toLowerCase();
            boolean hasClearIndicator = false;
            switch (category) {
                case HOODIE: hasClearIndicator = lower.contains("hoodie") || lower.contains("hood"); break;
                case TSHIRT: hasClearIndicator = lower.contains("tshirt") || lower.contains("t-shirt") || lower.contains("tee"); break;
                case SHIRT: hasClearIndicator = lower.contains("shirt") || lower.contains("blouse"); break;
                case PANTS: hasClearIndicator = lower.contains("pants") || lower.contains("jean") || lower.contains("trouser"); break;
                case SKIRT: hasClearIndicator = lower.contains("skirt"); break;
                case DRESS: hasClearIndicator = lower.contains("dress") || lower.contains("gown"); break;
                case JACKET: hasClearIndicator = lower.contains("jacket") || lower.contains("coat") || lower.contains("blazer"); break;
            }
            baseConfidence += hasClearIndicator ? 0.2 : 0.05;
        }
        return Math.min(baseConfidence, 0.95);
    }
}

