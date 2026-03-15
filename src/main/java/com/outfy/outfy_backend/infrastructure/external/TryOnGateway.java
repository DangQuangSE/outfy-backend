package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;

public interface TryOnGateway {
    TryOnResult generate(Long bodyProfileId, Long clothingItemId);
}

