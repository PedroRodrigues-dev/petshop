package com.pedro.petshop.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClientDTO {
    private Long id;
    private String name;
    private String cpf;
    private String image;
    private LocalDateTime registrationDate;
}
