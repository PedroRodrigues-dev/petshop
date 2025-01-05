package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import com.pedro.petshop.dtos.ClientDTO;
import com.pedro.petshop.entities.Client;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.ClientMapper;
import com.pedro.petshop.services.ClientService;

@SpringBootTest
class ClientControllerTest {

    @Autowired
    private ClientController clientController;

    @Autowired
    private ClientMapper clientMapper;

    @MockitoBean
    private ClientService clientService;

    @Test
    void testUploadProfileImage_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(clientService.existsByIdAndCpf(1L, "12345678900")).thenReturn(true);
        when(clientService.uploadImage(1L, file)).thenReturn(true);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        HttpStatus response = clientController.uploadProfileImage(1L, file);

        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void testUploadProfileImage_ClientNotFound() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(clientService.existsByIdAndCpf(1L, "12345678900")).thenReturn(false);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        HttpStatus response = clientController.uploadProfileImage(1L, file);

        assertEquals(HttpStatus.NOT_FOUND, response);
    }

    @Test
    void testUploadProfileImage_SaveFailed() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(clientService.existsByIdAndCpf(1L, "12345678900")).thenReturn(true);
        when(clientService.uploadImage(1L, file)).thenReturn(false);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        HttpStatus response = clientController.uploadProfileImage(1L, file);

        assertEquals(HttpStatus.NOT_FOUND, response);
    }

    @Test
    void testGetProfileImage_Success() {
        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn("image.jpg");

        when(clientService.existsByIdAndCpf(1L, "12345678900")).thenReturn(true);
        when(clientService.getProfileImage(1L)).thenReturn(Optional.of(mockResource));

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<Resource> response = clientController.getProfileImage(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Resource body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("image.jpg", body.getFilename());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
    }

    @Test
    void testGetProfileImage_ClientNotFound() {
        when(clientService.existsByIdAndCpf(1L, "12345678900")).thenReturn(false);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<Resource> response = clientController.getProfileImage(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetProfileImage_ResourceNotFound() {
        when(clientService.existsByIdAndCpf(1L, "12345678900")).thenReturn(true);
        when(clientService.getProfileImage(1L)).thenReturn(Optional.empty());

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<Resource> response = clientController.getProfileImage(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllClients() {
        ClientDTO breedDTO1 = createClient(1L, "John Doe", "12345678900");
        ClientDTO breedDTO2 = createClient(2L, "Jane Doe", "98765432100");
        Client breed1 = clientMapper.toEntity(breedDTO1);
        Client breed2 = clientMapper.toEntity(breedDTO2);

        Page<Client> mockPage = new PageImpl<>(List.of(breed1, breed2), PageRequest.of(0, 10), 2);

        when(clientService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        Page<ClientDTO> result = clientController.getAllClients(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        assertEquals("Jane Doe", result.getContent().get(1).getName());
    }

    @Test
    void testGetClientById_ClientExists() {
        ClientDTO mockClientDTO = createClient(1L, "John Doe", "12345678900");
        Client mockClient = clientMapper.toEntity(mockClientDTO);

        when(clientService.getByIdAndCpf(1L, "12345678900")).thenReturn(Optional.of(mockClient));

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<ClientDTO> response = clientController.getClientById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ClientDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("John Doe", body.getName());
        assertEquals("12345678900", body.getCpf());
    }

    @Test
    void testGetClientById_ClientNotFound() {
        when(clientService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ClientDTO> response = clientController.getClientById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateClient() {
        ClientDTO mockClientDTO = createClient(null, "John Doe", "12345678900");
        Client mockClient = clientMapper.toEntity(mockClientDTO);
        when(clientService.create(any(Client.class))).thenReturn(mockClient);

        ClientDTO clientToCreate = createClient(null, "John Doe", "12345678900");

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<ClientDTO> result = clientController.createClient(clientToCreate);

        ClientDTO body = Optional.ofNullable(result.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));

        assertEquals("John Doe", body.getName());
        assertEquals("12345678900", body.getCpf());
    }

    @Test
    void testUpdateClient() {
        ClientDTO mockClientDTO = createClient(1L, "John Doe", "12345678900");
        Client mockClient = clientMapper.toEntity(mockClientDTO);
        when(clientService.update(any(Long.class), any(Client.class))).thenReturn(mockClient);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ClientDTO clientToUpdate = createClient(1L, "John Doe", "12345678900");

        ClientDTO result = clientController.updateClient(1L, clientToUpdate);

        assertEquals("John Doe", result.getName());
    }

    @Test
    void testDeleteClient() {
        when(clientService.delete(1L)).thenReturn(true);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        boolean result = clientController.deleteClient(1L);

        assertEquals(true, result);
    }

    private ClientDTO createClient(Long id, String name, String cpf) {
        ClientDTO client = new ClientDTO();
        client.setId(id);
        client.setName(name);
        client.setCpf(cpf);
        return client;
    }
}
