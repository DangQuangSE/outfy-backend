package com.outfy.outfy_backend.modules.bodyprofile.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.bodyprofile.dto.request.CreateBodyProfileRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.request.GenerateAvatarRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyGenerationResult;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyProfileResponse;
import com.outfy.outfy_backend.modules.bodyprofile.service.BodyProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/body-profiles")
public class BodyProfileController {

    private final BodyProfileService bodyProfileService;

    public BodyProfileController(BodyProfileService bodyProfileService) {
        this.bodyProfileService = bodyProfileService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BodyProfileResponse>> createBodyProfile(
            @Valid @RequestBody CreateBodyProfileRequest request) {
        BodyProfileResponse response = bodyProfileService.createBodyProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Body profile created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BodyProfileResponse>> getBodyProfile(@PathVariable Long id) {
        BodyProfileResponse response = bodyProfileService.getBodyProfileById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BodyProfileResponse>>> getBodyProfilesByUserId(
            @PathVariable Long userId) {
        List<BodyProfileResponse> responses = bodyProfileService.getBodyProfilesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<ApiResponse<BodyGenerationResult>> generateAvatar(@PathVariable Long id) {
        BodyGenerationResult result = bodyProfileService.generateAvatar(id);
        return ResponseEntity.ok(ApiResponse.success("Avatar generated successfully", result));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<BodyGenerationResult>> getAvatarResult(@PathVariable Long id) {
        BodyGenerationResult result = bodyProfileService.getAvatarResult(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Generate avatar directly from measurements (for demo without database)
     * This is the main endpoint for the 3D body pipeline demo
     */
    @PostMapping("/generate-avatar")
    public ResponseEntity<ApiResponse<BodyGenerationResult>> generateAvatarDirect(
            @Valid @RequestBody GenerateAvatarRequest request) {
        BodyGenerationResult result = bodyProfileService.generateAvatarDirect(request);
        return ResponseEntity.ok(ApiResponse.success("Avatar generated successfully", result));
    }
}

