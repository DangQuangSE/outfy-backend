package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;

public interface TryOnGateway {

    /**
     * Generate try-on result
     * @param avatarId avatar identifier (e.g., "slim", "regular", "athletic", "plus")
     * @param clothingItemId clothing item ID
     * @param size optional size (e.g., "S", "M", "L", "XL")
     * @param fitType optional fit type (e.g., "slim", "regular", "loose")
     * @return TryOnResult with preview URL and metadata
     */
    TryOnResult generate(String avatarId, Long clothingItemId, String size, String fitType);
}

