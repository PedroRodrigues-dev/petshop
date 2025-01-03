package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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

import com.pedro.petshop.entities.Breed;
import com.pedro.petshop.services.BreedService;

@SpringBootTest
class BreedControllerTest {

    @Autowired
    private BreedController breedController;

    @MockitoBean
    private BreedService breedService;

    @Test
    void testGetAllBreedsPaged() {
        List<Breed> mockBreeds = Arrays.asList(
                createBreed(1L, "Labrador"),
                createBreed(2L, "Poodle"));

        Page<Breed> mockPage = new PageImpl<>(mockBreeds, PageRequest.of(0, 10), mockBreeds.size());

        when(breedService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Breed> result = breedController.getAllBreeds(pageable);
        assertEquals(2, result.getContent().size());
        assertEquals("Labrador", result.getContent().get(0).getDescription());
        assertEquals("Poodle", result.getContent().get(1).getDescription());

        assertEquals(10, result.getSize());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testGetBreedById_BreedExists() {
        Breed mockBreed = createBreed(1L, "Labrador");
        when(breedService.findById(1L)).thenReturn(Optional.of(mockBreed));

        ResponseEntity<Breed> response = breedController.getBreedById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Breed body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Labrador", body.getDescription());
    }

    @Test
    void testGetBreedById_BreedNotFound() {
        when(breedService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Breed> response = breedController.getBreedById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateBreed() {
        Breed mockBreed = createBreed(null, "Labrador");
        when(breedService.create(any(Breed.class))).thenReturn(mockBreed);

        Breed breedToCreate = createBreed(null, "Labrador");

        Breed result = breedController.createBreed(breedToCreate);

        assertEquals("Labrador", result.getDescription());
    }

    @Test
    void testUpdateBreed() {
        Breed mockBreed = createBreed(1L, "Labrador");
        when(breedService.update(any(Long.class), any(Breed.class))).thenReturn(mockBreed);

        Breed breedToUpdate = createBreed(1L, "Labrador");

        Breed result = breedController.updateBreed(1L, breedToUpdate);

        assertEquals("Labrador", result.getDescription());
    }

    @Test
    void testDeleteBreed() {
        when(breedService.delete(1L)).thenReturn(true);

        boolean result = breedController.deleteBreed(1L);

        assertEquals(true, result);
    }

    private Breed createBreed(Long id, String description) {
        Breed breed = new Breed();
        breed.setId(id);
        breed.setDescription(description);
        return breed;
    }
}
