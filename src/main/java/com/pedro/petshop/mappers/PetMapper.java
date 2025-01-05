package com.pedro.petshop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.pedro.petshop.dtos.PetDTO;
import com.pedro.petshop.entities.Pet;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "breed.id", target = "breedId")
    PetDTO toDto(Pet pet);

    @Mapping(source = "clientId", target = "client.id")
    @Mapping(source = "breedId", target = "breed.id")
    @Mapping(target = "image", ignore = true)
    Pet toEntity(PetDTO petDTO);

    default Page<PetDTO> pageToPageDTO(Page<Pet> petPage) {
        return petPage.map(this::toDto);
    }
}
