package com.pedro.petshop.dtos;

import lombok.Data;

@Data
public class ContactDTO {
    private Long id;
    private Long clientId;
    private String tag;
    private String type;
    private String value;
}
