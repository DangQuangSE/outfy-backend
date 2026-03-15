package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;

public interface BodyGenerationGateway {
    BodyGenerationResult generate(Long bodyProfileId);
}

