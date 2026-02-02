package com.example.backend.mapper;

import com.example.backend.dto.RegisterResponse;
import com.example.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegisterResponse toRegisterResponse(User user);

}
