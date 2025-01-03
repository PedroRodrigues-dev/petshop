package com.pedro.petshop.dtos;

import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private String street;
    private String city;
    private String neighborhood;
    private String complement;
    private String tag;
    private Long clientId;
}
