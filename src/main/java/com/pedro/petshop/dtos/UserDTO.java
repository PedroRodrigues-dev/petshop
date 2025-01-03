package com.pedro.petshop.dtos;

import com.pedro.petshop.enums.Role;

import lombok.Data;

@Data
public class UserDTO {
    private String cpf;
    private String name;
    private Role role;
    private String password;
}
