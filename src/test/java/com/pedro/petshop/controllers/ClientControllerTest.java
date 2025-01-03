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

import com.pedro.petshop.dtos.ClientDTO;
import com.pedro.petshop.entities.Client;
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
    void testGetAllClients() {
        ClientDTO breedDTO1 = createClient(1L, "John Doe", "12345678900");
        ClientDTO breedDTO2 = createClient(2L, "Jane Doe", "98765432100");
        Client breed1 = clientMapper.toEntity(breedDTO1);
        Client breed2 = clientMapper.toEntity(breedDTO2);

        Page<Client> mockPage = new PageImpl<>(List.of(breed1, breed2), PageRequest.of(0, 10), 2);

        when(clientService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientDTO> result = clientController.getAllClients(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        assertEquals("Jane Doe", result.getContent().get(1).getName());
    }

    @Test
    void testGetClientById_ClientExists() {
        ClientDTO mockClientDTO = createClient(1L, "John Doe", "12345678900");
        Client mockClient = clientMapper.toEntity(mockClientDTO);
        when(clientService.findById(1L)).thenReturn(Optional.of(mockClient));

        ResponseEntity<ClientDTO> response = clientController.getClientById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ClientDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("John Doe", body.getName());
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

        ClientDTO result = clientController.createClient(clientToCreate);

        assertEquals("John Doe", result.getName());
        assertEquals("12345678900", result.getCpf());
    }

    @Test
    void testUpdateClient() {
        ClientDTO mockClientDTO = createClient(1L, "John Doe", "12345678900");
        Client mockClient = clientMapper.toEntity(mockClientDTO);
        when(clientService.update(any(Long.class), any(Client.class))).thenReturn(mockClient);

        ClientDTO clientToUpdate = createClient(1L, "John Doe", "12345678900");

        ClientDTO result = clientController.updateClient(1L, clientToUpdate);

        assertEquals("John Doe", result.getName());
    }

    @Test
    void testDeleteClient() {
        when(clientService.delete(1L)).thenReturn(true);

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
