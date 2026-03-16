package com.outfy.outfy_backend.modules.tryon.mapper;

import com.outfy.outfy_backend.modules.tryon.dto.request.CreateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.UpdateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnSessionResponse;
import com.outfy.outfy_backend.modules.tryon.entity.TryOnSession;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TryOnMapper {

    TryOnSession toEntity(CreateTryOnSessionRequest request);

    TryOnSessionResponse toResponse(TryOnSession entity);

    void updateFromRequest(UpdateTryOnSessionRequest request, @MappingTarget TryOnSession entity);
}

