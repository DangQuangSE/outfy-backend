package com.outfy.outfy_backend.modules.tryon.mapper;

import com.outfy.outfy_backend.modules.tryon.dto.request.CreateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.request.UpdateTryOnSessionRequest;
import com.outfy.outfy_backend.modules.tryon.dto.response.TryOnSessionResponse;
import com.outfy.outfy_backend.modules.tryon.entity.TryOnSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TryOnMapper {

    // Ignore clothingItemIds as it's handled manually in service (converted to JSON)
    @Mapping(target = "clothingItemIdsJson", ignore = true)
    @Mapping(target = "clothingItemId", ignore = true)
    TryOnSession toEntity(CreateTryOnSessionRequest request);

    TryOnSessionResponse toResponse(TryOnSession entity);

    void updateFromRequest(UpdateTryOnSessionRequest request, @MappingTarget TryOnSession entity);
}

