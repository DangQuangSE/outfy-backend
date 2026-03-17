package com.outfy.outfy_backend.modules.clothing.mapper;

import com.outfy.outfy_backend.modules.clothing.dto.request.CreateClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingItemResponse;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClothingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "garmentCategory", ignore = true)
    @Mapping(target = "templateCode", ignore = true)
    @Mapping(target = "modelUrl", ignore = true)
    @Mapping(target = "previewUrl", ignore = true)
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "draftId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClothingItem toEntity(CreateClothingRequest request);

    ClothingItemResponse toResponse(ClothingItem entity);
}

