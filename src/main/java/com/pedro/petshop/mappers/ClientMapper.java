package com.pedro.petshop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.pedro.petshop.dtos.ClientDTO;
import com.pedro.petshop.entities.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO toDto(Client client);

    @Mapping(source = "cpf", target = "user.cpf")
    @Mapping(target = "image", ignore = true)
    Client toEntity(ClientDTO clientDTO);

    default Page<ClientDTO> pageToPageDTO(Page<Client> clientPage) {
        return clientPage.map(this::toDto);
    }
}
