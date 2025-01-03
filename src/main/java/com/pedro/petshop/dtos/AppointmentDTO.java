package com.pedro.petshop.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AppointmentDTO {
    private Long id;
    private Long petId;
    private String description;
    private Double cost;
    private LocalDateTime date;
}
