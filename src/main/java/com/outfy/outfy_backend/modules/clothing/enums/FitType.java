package com.outfy.outfy_backend.modules.clothing.enums;

/**
 * Enum representing fit types for clothing
 */
public enum FitType {
    SLIM,
    REGULAR,
    LOOSE;

    /**
     * Parse fit type from filename or attributes
     */
    public static FitType fromFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return REGULAR; // default
        }

        String lower = filename.toLowerCase();

        if (lower.contains("slim") || lower.contains("skinny") || lower.contains("tight")) {
            return SLIM;
        } else if (lower.contains("loose") || lower.contains("oversize") || lower.contains("baggy")) {
            return LOOSE;
        }

        return REGULAR; // default
    }
}

