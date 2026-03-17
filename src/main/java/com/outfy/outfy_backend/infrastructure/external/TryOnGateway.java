package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import java.util.List;

public interface TryOnGateway {

    /**
     * Generate try-on result with multiple clothing items
     * @param avatarId avatar identifier (e.g., "slim_male", "regular_female", "curvy_female")
     * @param clothingItemIds list of clothing item IDs to try on together
     * @param size optional size (e.g., "S", "M", "L", "XL")
     * @param fitType optional fit type (e.g., "slim", "regular", "loose")
     * @return TryOnResult with preview URL and metadata
     */
    TryOnResult generate(String avatarId, List<Long> clothingItemIds, String size, String fitType);
}

