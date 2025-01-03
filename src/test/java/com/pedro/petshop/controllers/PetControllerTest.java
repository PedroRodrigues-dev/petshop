package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
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

import com.pedro.petshop.entities.Pet;
import com.pedro.petshop.services.PetService;

@SpringBootTest
class PetControllerTest {

    @Autowired
    private PetController petController;

    @MockitoBean
    private PetService petService;

    @Test
    void testCreatePet() {
        Pet mockPet = createPet(null, "Buddy", LocalDate.of(2020, 5, 15));
        when(petService.create(any(Pet.class))).thenReturn(mockPet);

        Pet petToCreate = createPet(null, "Buddy", LocalDate.of(2020, 5, 15));

        Pet result = petController.createPet(petToCreate);

        assertEquals("Buddy", result.getName());
        assertEquals(LocalDate.of(2020, 5, 15), result.getBirthDate());
    }

    @Test
    void testGetPetById_PetExists() {
        Pet mockPet = createPet(1L, "Buddy", LocalDate.of(2020, 5, 15));
        when(petService.findById(1L)).thenReturn(Optional.of(mockPet));

        ResponseEntity<Pet> response = petController.getPetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Pet body = response.getBody();
        assertEquals("Buddy", body.getName());
        assertEquals(LocalDate.of(2020, 5, 15), body.getBirthDate());
    }

    @Test
    void testGetPetById_PetNotFound() {
        when(petService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Pet> response = petController.getPetById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllPets() {
        Pet mockPet1 = createPet(1L, "Buddy", LocalDate.of(2020, 5, 15));
        Pet mockPet2 = createPet(2L, "Bella", LocalDate.of(2019, 8, 12));
        Page<Pet> mockPage = new PageImpl<>(Arrays.asList(mockPet1, mockPet2), PageRequest.of(0, 10), 2);

        when(petService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Pet> result = petController.getAllPets(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Buddy", result.getContent().get(0).getName());
        assertEquals("Bella", result.getContent().get(1).getName());
    }

    @Test
    void testUpdatePet() {
        Pet mockPet = createPet(1L, "Buddy", LocalDate.of(2020, 5, 15));
        when(petService.update(any(Long.class), any(Pet.class))).thenReturn(mockPet);

        Pet petToUpdate = createPet(1L, "Buddy", LocalDate.of(2020, 5, 15));

        Pet result = petController.updatePet(1L, petToUpdate);

        assertEquals("Buddy", result.getName());
        assertEquals(LocalDate.of(2020, 5, 15), result.getBirthDate());
    }

    @Test
    void testDeletePet() {
        when(petService.delete(1L)).thenReturn(true);

        boolean result = petController.deletePet(1L);

        assertEquals(true, result);
    }

    private Pet createPet(Long id, String name, LocalDate birthDate) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setBirthDate(birthDate);
        return pet;
    }
}
