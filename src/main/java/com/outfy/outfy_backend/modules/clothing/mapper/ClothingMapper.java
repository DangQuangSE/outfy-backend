package com.outfy.outfy_backend.modules.clothing.mapper;

import com.outfy.outfy_backend.modules.clothing.dto.request.CreateClothingRequest;
import com.outfy.outfy_backend.modules.clothing.dto.response.ClothingItemResponse;
import com.outfy.outfy_backend.modules.clothing.entity.ClothingItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClothingMapper {

    ClothingItem toEntity(CreateClothingRequest request);

    ClothingItemResponse toResponse(ClothingItem entity);
}

