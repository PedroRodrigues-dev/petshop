package com.pedro.petshop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.pedro.petshop.dtos.RegisterDTO;
import com.pedro.petshop.dtos.UserDTO;
import com.pedro.petshop.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", expression = "java(\"\")")
    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    @Mapping(target = "role", ignore = true)
    User registerToUser(RegisterDTO register);

    default Page<UserDTO> pageToPageDTO(Page<User> userPage) {
        return userPage.map(this::toDto);
    }
}
