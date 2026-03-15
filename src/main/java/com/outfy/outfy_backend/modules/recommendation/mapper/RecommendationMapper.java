package com.outfy.outfy_backend.modules.recommendation.mapper;

import com.outfy.outfy_backend.modules.recommendation.dto.request.CreateRecommendationRequest;
import com.outfy.outfy_backend.modules.recommendation.dto.response.RecommendationSessionResponse;
import com.outfy.outfy_backend.modules.recommendation.entity.RecommendationSession;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecommendationMapper {

    RecommendationSession toEntity(CreateRecommendationRequest request);

    RecommendationSessionResponse toResponse(RecommendationSession entity);
}

