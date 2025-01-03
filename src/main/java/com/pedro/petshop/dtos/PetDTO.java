package com.pedro.petshop.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PetDTO {
    private Long id;
    private Long clientId;
    private Long breedId;
    private String image;
    private String name;
    private LocalDate birthDate;
}
