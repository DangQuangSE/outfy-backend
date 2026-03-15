package com.outfy.outfy_backend.modules.recommendation.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.recommendation.dto.request.CreateRecommendationRequest;
import com.outfy.outfy_backend.modules.recommendation.dto.response.RecommendationSessionResponse;
import com.outfy.outfy_backend.modules.recommendation.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecommendationSessionResponse>> createRecommendation(
            @Valid @RequestBody CreateRecommendationRequest request) {
        RecommendationSessionResponse response = recommendationService.createRecommendation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recommendation session created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecommendationSessionResponse>> getRecommendation(@PathVariable Long id) {
        RecommendationSessionResponse response = recommendationService.getRecommendationById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RecommendationSessionResponse>>> getRecommendationsByUserId(
            @PathVariable Long userId) {
        List<RecommendationSessionResponse> responses = recommendationService.getRecommendationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<ApiResponse<RecommendationSessionResponse>> generateRecommendations(@PathVariable Long id) {
        RecommendationSessionResponse response = recommendationService.generateRecommendations(id);
        return ResponseEntity.ok(ApiResponse.success("Recommendations generated successfully", response));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<RecommendationSessionResponse>> getRecommendationWithItems(@PathVariable Long id) {
        RecommendationSessionResponse response = recommendationService.getRecommendationWithItems(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

