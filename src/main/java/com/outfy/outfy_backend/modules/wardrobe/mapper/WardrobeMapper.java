package com.outfy.outfy_backend.modules.wardrobe.mapper;

import com.outfy.outfy_backend.modules.wardrobe.dto.request.CreateWardrobeItemRequest;
import com.outfy.outfy_backend.modules.wardrobe.dto.response.WardrobeItemResponse;
import com.outfy.outfy_backend.modules.wardrobe.entity.WardrobeItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WardrobeMapper {

    WardrobeItem toEntity(CreateWardrobeItemRequest request);

    WardrobeItemResponse toResponse(WardrobeItem entity);
}

