package com.outfy.outfy_backend.modules.bodyprofile.interfaces;

import com.outfy.outfy_backend.modules.bodyprofile.dto.request.CreateBodyProfileRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.request.GenerateAvatarRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyProfileResponse;

import java.util.List;

/**
 * Interface for BodyProfile service operations
 */
public interface IBodyProfileService {

    /**
     * Create a new body profile
     */
    BodyProfileResponse createBodyProfile(CreateBodyProfileRequest request);

    /**
     * Get body profile by ID
     */
    BodyProfileResponse getBodyProfileById(Long id);

    /**
     * Get all body profiles for a user
     */
    List<BodyProfileResponse> getBodyProfilesByUserId(Long userId);

    /**
     * Generate avatar for an existing body profile
     */
    BodyGenerationResult generateAvatar(Long profileId);

    /**
     * Get avatar generation result
     */
    BodyGenerationResult getAvatarResult(Long profileId);

    /**
     * Generate avatar directly from measurements (for demo without database)
     */
    BodyGenerationResult generateAvatarDirect(GenerateAvatarRequest request);
}

