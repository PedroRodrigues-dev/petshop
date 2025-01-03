package com.pedro.petshop.dtos;

import lombok.Data;

@Data
public class RegisterDTO {
    private String cpf;
    private String name;
    private String password;
}
