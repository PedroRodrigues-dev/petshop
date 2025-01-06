package com.pedro.petshop.mappers;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import com.pedro.petshop.dtos.BreedDTO;
import com.pedro.petshop.entities.Breed;

@Mapper(componentModel = "spring")
public interface BreedMapper {

    BreedDTO toDto(Breed breed);

    Breed toEntity(BreedDTO breedDTO);

    default Page<BreedDTO> pageToPageDTO(Page<Breed> breedPage) {
        return breedPage.map(this::toDto);
    }
}
