package com.outfy.outfy_backend.modules.auth.mapper;

import com.outfy.outfy_backend.modules.auth.dto.request.RegisterRequest;
import com.outfy.outfy_backend.modules.auth.dto.response.UserResponse;
import com.outfy.outfy_backend.modules.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toEntity(RegisterRequest request);

    UserResponse toResponse(User entity);
}

