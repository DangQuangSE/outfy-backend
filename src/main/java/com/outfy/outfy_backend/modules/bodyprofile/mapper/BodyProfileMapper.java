package com.outfy.outfy_backend.modules.bodyprofile.mapper;

import com.outfy.outfy_backend.modules.bodyprofile.dto.request.CreateBodyProfileRequest;
import com.outfy.outfy_backend.modules.bodyprofile.dto.response.BodyProfileResponse;
import com.outfy.outfy_backend.modules.bodyprofile.entity.BodyProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BodyProfileMapper {

    BodyProfile toEntity(CreateBodyProfileRequest request);

    BodyProfileResponse toResponse(BodyProfile entity);
}

