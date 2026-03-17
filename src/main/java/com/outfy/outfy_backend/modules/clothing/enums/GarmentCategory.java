package com.outfy.outfy_backend.modules.clothing.enums;

/**
 * Enum representing garment categories for clothing classification
 */
public enum GarmentCategory {
    TSHIRT("tshirt_template"),
    HOODIE("hoodie_template"),
    SHIRT("shirt_template"),
    PANTS("pants_template"),
    SHORTS("shorts_template"),
    SKIRT("skirt_template"),
    SHORT_SKIRT("short_skirt_template"),
    DRESS("dress_template"),
    JACKET("jacket_template"),
    CLOTH_TOP("cloth_top_template"),
    FEMALE_SHIRT("female_shirt_template");

    private final String templateCode;

    GarmentCategory(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    /**
     * Parse garment category from filename or URL
     */
    public static GarmentCategory fromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return GarmentCategory.TSHIRT; // default
        }

        String lower = filename.toLowerCase();

        if (lower.contains("hoodie") || lower.contains("hood")) {
            return HOODIE;
        } else if (lower.contains("tshirt") || lower.contains("t-shirt") || lower.contains("tee")) {
            return TSHIRT;
        } else if (lower.contains("shirt") || lower.contains("blouse")) {
            return SHIRT;
        } else if (lower.contains("pants") || lower.contains("jean") || lower.contains("trouser") || lower.contains("jeans")) {
            return PANTS;
        } else if (lower.contains("skirt")) {
            return SKIRT;
        } else if (lower.contains("dress") || lower.contains("gown")) {
            return DRESS;
        } else if (lower.contains("shorts") || lower.contains("short")) {
            return SHORTS;
        } else if (lower.contains("jacket") || lower.contains("coat") || lower.contains("blazer")) {
            return JACKET;
        } else if (lower.contains("cloth_top") || lower.contains("crop_top") || lower.contains("crop top")) {
            return CLOTH_TOP;
        } else if (lower.contains("female_shirt") || lower.contains("woman_shirt") || lower.contains("blouse")) {
            return FEMALE_SHIRT;
        } else if (lower.contains("short_skirt") || lower.contains("mini_skirt")) {
            return SHORT_SKIRT;
        }

        return TSHIRT; // default
    }
}

