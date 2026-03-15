package com.outfy.outfy_backend.modules.tryon.controller;

import com.outfy.outfy_backend.common.constant.AppConstants;
import com.outfy.outfy_backend.common.response.ApiResponse;
import com.outfy.outfy_backend.modules.tryon.dto.request.CreateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnResult;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnSessionResponse;
import com.outfy.outfy_backend.modules.tryon.service.TryOnService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.API_V1 + "/tryons")
public class TryOnController {

    private final TryOnService tryOnService;

    public TryOnController(TryOnService tryOnService) {
        this.tryOnService = tryOnService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TryOnSessionResponse>> createTryOnSession(
            @Valid @RequestBody CreateTryOnSessionRequest request) {
        TryOnSessionResponse response = tryOnService.createTryOnSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Try-on session created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TryOnSessionResponse>> getTryOnSession(@PathVariable Long id) {
        TryOnSessionResponse response = tryOnService.getTryOnSessionById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TryOnSessionResponse>>> getTryOnSessionsByUserId(
            @PathVariable Long userId) {
        List<TryOnSessionResponse> responses = tryOnService.getTryOnSessionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<ApiResponse<TryOnResult>> generateTryOnResult(@PathVariable Long id) {
        TryOnResult result = tryOnService.generateTryOnResult(id);
        return ResponseEntity.ok(ApiResponse.success("Try-on result generated successfully", result));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<TryOnResult>> getTryOnResult(@PathVariable Long id) {
        TryOnResult result = tryOnService.getTryOnResult(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}

