package com.outfy.outfy_backend.infrastructure.external;

import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;

public interface BodyGenerationGateway {
    BodyGenerationResult generate(Long bodyProfileId);

    /**
     * Generate avatar directly from measurements (for demo without database)
     */
    BodyGenerationResult generateFromMeasurements(
            String gender,
            double heightCm, double weightKg,
            double chestCm, double waistCm, double hipCm,
            double shoulderCm, double inseamCm);
}

