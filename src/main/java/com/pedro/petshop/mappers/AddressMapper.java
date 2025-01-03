package com.pedro.petshop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.pedro.petshop.dtos.AddressDTO;
import com.pedro.petshop.entities.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "client.id", target = "clientId")
    AddressDTO toDto(Address address);

    @Mapping(source = "clientId", target = "client.id")
    Address toEntity(AddressDTO addressDTO);

    default Page<AddressDTO> pageToPageDTO(Page<Address> addressPage) {
        return addressPage.map(this::toDto);
    }
}
