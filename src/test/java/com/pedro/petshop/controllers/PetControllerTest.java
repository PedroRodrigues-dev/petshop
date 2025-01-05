package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pedro.petshop.configs.CustomAuthentication;
import com.pedro.petshop.dtos.PetDTO;
import com.pedro.petshop.entities.Pet;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.PetMapper;
import com.pedro.petshop.services.PetService;

@SpringBootTest
class PetControllerTest {

    @Autowired
    private PetController petController;

    @Autowired
    private PetMapper petMapper;

    @MockitoBean
    private PetService petService;

    @Test
    void testUploadProfileImage_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(petService.existsByIdAndUserCpf(1L, "12345678900")).thenReturn(true);
        when(petService.uploadImage(1L, file)).thenReturn(true);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        HttpStatus response = petController.uploadProfileImage(1L, file);

        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void testUploadProfileImage_ClientNotFound() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(petService.existsByIdAndUserCpf(1L, "12345678900")).thenReturn(false);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        HttpStatus response = petController.uploadProfileImage(1L, file);

        assertEquals(HttpStatus.NOT_FOUND, response);
    }

    @Test
    void testUploadProfileImage_SaveFailed() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(petService.existsByIdAndUserCpf(1L, "12345678900")).thenReturn(true);
        when(petService.uploadImage(1L, file)).thenReturn(false);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        HttpStatus response = petController.uploadProfileImage(1L, file);

        assertEquals(HttpStatus.NOT_FOUND, response);
    }

    @Test
    void testGetProfileImage_Success() {
        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn("image.jpg");

        when(petService.existsByIdAndUserCpf(1L, "12345678900")).thenReturn(true);
        when(petService.getProfileImage(1L)).thenReturn(Optional.of(mockResource));

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<Resource> response = petController.getProfileImage(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Resource body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("image.jpg", body.getFilename());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
    }

    @Test
    void testGetProfileImage_ClientNotFound() {
        when(petService.existsByIdAndUserCpf(1L, "12345678900")).thenReturn(false);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<Resource> response = petController.getProfileImage(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetProfileImage_ResourceNotFound() {
        when(petService.existsByIdAndUserCpf(1L, "12345678900")).thenReturn(true);
        when(petService.getProfileImage(1L)).thenReturn(Optional.empty());

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<Resource> response = petController.getProfileImage(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreatePet() {
        PetDTO mockPetDTO = createPet(null, "Buddy", LocalDate.of(2020, 5, 15));
        Pet mockPet = petMapper.toEntity(mockPetDTO);
        when(petService.create(any(Pet.class))).thenReturn(mockPet);

        PetDTO petToCreate = createPet(null, "Buddy", LocalDate.of(2020, 5, 15));

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<PetDTO> result = petController.createPet(petToCreate);

        PetDTO body = Optional.ofNullable(result.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));

        assertEquals("Buddy", body.getName());
        assertEquals(LocalDate.of(2020, 5, 15), body.getBirthDate());
    }

    @Test
    void testGetPetById_PetExists() {
        PetDTO mockPetDTO = createPet(null, "Buddy", LocalDate.of(2020, 5, 15));
        Pet mockPet = petMapper.toEntity(mockPetDTO);
        when(petService.getByIdAndUserCpf(1L, "12345678900")).thenReturn(Optional.of(mockPet));

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<PetDTO> response = petController.getPetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PetDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Buddy", body.getName());
        assertEquals(LocalDate.of(2020, 5, 15), body.getBirthDate());
    }

    @Test
    void testGetPetById_PetNotFound() {
        when(petService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<PetDTO> response = petController.getPetById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllPets() {
        PetDTO petDTO1 = createPet(1L, "Buddy", LocalDate.of(2020, 5, 15));
        PetDTO petDTO2 = createPet(2L, "Bella", LocalDate.of(2019, 8, 12));
        Pet pet1 = petMapper.toEntity(petDTO1);
        Pet pet2 = petMapper.toEntity(petDTO2);

        Page<Pet> mockPage = new PageImpl<>(List.of(pet1, pet2), PageRequest.of(0, 10), 2);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        when(petService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<PetDTO> result = petController.getAllPets(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Buddy", result.getContent().get(0).getName());
        assertEquals("Bella", result.getContent().get(1).getName());
    }

    @Test
    void testUpdatePet() {
        PetDTO mockPetDTO = createPet(1L, "Buddy", LocalDate.of(2020, 5, 15));
        Pet mockPet = petMapper.toEntity(mockPetDTO);
        when(petService.update(any(Long.class), any(Pet.class))).thenReturn(mockPet);

        PetDTO petToUpdate = createPet(1L, "Buddy", LocalDate.of(2020, 5, 15));

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        PetDTO result = petController.updatePet(1L, petToUpdate);

        assertEquals("Buddy", result.getName());
        assertEquals(LocalDate.of(2020, 5, 15), result.getBirthDate());
    }

    @Test
    void testDeletePet() {
        when(petService.delete(1L)).thenReturn(true);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        boolean result = petController.deletePet(1L);

        assertEquals(true, result);
    }

    private PetDTO createPet(Long id, String name, LocalDate birthDate) {
        PetDTO pet = new PetDTO();
        pet.setId(id);
        pet.setName(name);
        pet.setBirthDate(birthDate);
        return pet;
    }
}
