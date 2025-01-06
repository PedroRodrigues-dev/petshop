package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pedro.petshop.dtos.BreedDTO;
import com.pedro.petshop.entities.Breed;
import com.pedro.petshop.mappers.BreedMapper;
import com.pedro.petshop.services.BreedService;

@SpringBootTest
class BreedControllerTest {

    @Autowired
    private BreedController breedController;

    @Autowired
    private BreedMapper breedMapper;

    @MockitoBean
    private BreedService breedService;

    @Test
    void testGetAllBreedsPaged() {
        BreedDTO breedDTO1 = createBreed(1L, "Labrador");
        BreedDTO breedDTO2 = createBreed(2L, "Poodle");
        Breed breed1 = breedMapper.toEntity(breedDTO1);
        Breed breed2 = breedMapper.toEntity(breedDTO2);

        Page<Breed> mockPage = new PageImpl<>(List.of(breed1, breed2), PageRequest.of(0, 10), 2);

        when(breedService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<BreedDTO> result = breedController.getAllBreeds(pageable);
        assertEquals(2, result.getContent().size());
        assertEquals("Labrador", result.getContent().get(0).getDescription());
        assertEquals("Poodle", result.getContent().get(1).getDescription());

        assertEquals(10, result.getSize());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testGetBreedById_BreedExists() {
        BreedDTO mockBreedDTO = createBreed(1L, "Labrador");
        Breed mockBreed = breedMapper.toEntity(mockBreedDTO);
        when(breedService.findById(1L)).thenReturn(Optional.of(mockBreed));

        ResponseEntity<BreedDTO> response = breedController.getBreedById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BreedDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Labrador", body.getDescription());
    }

    @Test
    void testGetBreedById_BreedNotFound() {
        when(breedService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<BreedDTO> response = breedController.getBreedById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateBreed() {
        BreedDTO mockBreedDTO = createBreed(null, "Labrador");
        Breed mockBreed = breedMapper.toEntity(mockBreedDTO);
        when(breedService.create(any(Breed.class))).thenReturn(mockBreed);

        BreedDTO breedToCreate = createBreed(null, "Labrador");

        ResponseEntity<BreedDTO> result = breedController.createBreed(breedToCreate);

        BreedDTO body = Optional.ofNullable(result.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));

        assertEquals("Labrador", body.getDescription());
    }

    @Test
    void testUpdateBreed() {
        BreedDTO mockBreedDTO = createBreed(1L, "Labrador");
        Breed mockBreed = breedMapper.toEntity(mockBreedDTO);
        when(breedService.update(any(Long.class), any(Breed.class))).thenReturn(mockBreed);

        BreedDTO breedToUpdate = createBreed(1L, "Labrador");

        BreedDTO result = breedController.updateBreed(1L, breedToUpdate);

        assertEquals("Labrador", result.getDescription());
    }

    @Test
    void testDeleteBreed() {
        when(breedService.delete(1L)).thenReturn(true);

        boolean result = breedController.deleteBreed(1L);

        assertEquals(true, result);
    }

    private BreedDTO createBreed(Long id, String description) {
        BreedDTO breed = new BreedDTO();
        breed.setId(id);
        breed.setDescription(description);
        return breed;
    }
}
