package com.outfy.outfy_backend.modules.clothing.enums;

/**
 * Enum representing sleeve types for clothing
 */
public enum SleeveType {
    SHORT,
    LONG,
    NONE;

    /**
     * Parse sleeve type from filename or attributes
     */
    public static SleeveType fromFilename(String filename, GarmentCategory category) {
        if (filename == null || filename.isBlank()) {
            return getDefaultForCategory(category);
        }

        String lower = filename.toLowerCase();

        // Check for explicit sleeve indicators
        if (lower.contains("short") || lower.contains("sleeve-less") || lower.contains("sleeveless")) {
            return SHORT;
        } else if (lower.contains("long") || lower.contains("lng")) {
            return LONG;
        }

        // Default based on category
        return getDefaultForCategory(category);
    }

    private static SleeveType getDefaultForCategory(GarmentCategory category) {
        if (category == null) {
            return SHORT;
        }

        switch (category) {
            case TSHIRT:
            case SHIRT:
                return SHORT;
            case HOODIE:
            case JACKET:
                return LONG;
            case PANTS:
            case SKIRT:
            case DRESS:
                return NONE;
            default:
                return SHORT;
        }
    }
}

