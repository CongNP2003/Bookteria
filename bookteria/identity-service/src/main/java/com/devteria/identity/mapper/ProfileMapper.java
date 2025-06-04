package com.devteria.identity.mapper;

import com.devteria.identity.dto.request.ProfileCreatRequest;
import com.devteria.identity.dto.request.UserCreationRequest;
import org.mapstruct.Mapper;

// đoạn này map cho bên ProfileClien cho service Profile
@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreatRequest toProfileCreatRequest (UserCreationRequest request);
}
