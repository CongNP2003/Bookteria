package com.devteria.identity.mapper;

import org.mapstruct.Mapper;

import com.devteria.identity.dto.request.ProfileCreatRequest;
import com.devteria.identity.dto.request.UserCreationRequest;

// đoạn này map cho bên ProfileClien cho service Profile
@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreatRequest toProfileCreatRequest(UserCreationRequest request);
}
