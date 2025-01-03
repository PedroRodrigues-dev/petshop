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

import com.pedro.petshop.entities.Client;
import com.pedro.petshop.services.ClientService;

@SpringBootTest
class ClientControllerTest {

    @Autowired
    private ClientController clientController;

    @MockitoBean
    private ClientService clientService;

    @Test
    void testGetAllClients() {
        List<Client> mockClients = Arrays.asList(
                createClient(1L, "John Doe", "12345678900"),
                createClient(2L, "Jane Doe", "98765432100"));

        Page<Client> mockPage = new PageImpl<>(mockClients, PageRequest.of(0, 10), mockClients.size());

        when(clientService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Client> result = clientController.getAllClients(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        assertEquals("Jane Doe", result.getContent().get(1).getName());
    }

    @Test
    void testGetClientById_ClientExists() {
        Client mockClient = createClient(1L, "John Doe", "12345678900");
        when(clientService.findById(1L)).thenReturn(Optional.of(mockClient));

        ResponseEntity<Client> response = clientController.getClientById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Client body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("John Doe", body.getName());
    }

    @Test
    void testGetClientById_ClientNotFound() {
        when(clientService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Client> response = clientController.getClientById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateClient() {
        Client mockClient = createClient(null, "John Doe", "12345678900");
        when(clientService.create(any(Client.class))).thenReturn(mockClient);

        Client clientToCreate = createClient(null, "John Doe", "12345678900");

        Client result = clientController.createClient(clientToCreate);

        assertEquals("John Doe", result.getName());
        assertEquals("12345678900", result.getCpf());
    }

    @Test
    void testUpdateClient() {
        Client mockClient = createClient(1L, "John Doe", "12345678900");
        when(clientService.update(any(Long.class), any(Client.class))).thenReturn(mockClient);

        Client clientToUpdate = createClient(1L, "John Doe", "12345678900");

        Client result = clientController.updateClient(1L, clientToUpdate);

        assertEquals("John Doe", result.getName());
    }

    @Test
    void testDeleteClient() {
        when(clientService.delete(1L)).thenReturn(true);

        boolean result = clientController.deleteClient(1L);

        assertEquals(true, result);
    }

    private Client createClient(Long id, String name, String cpf) {
        Client client = new Client();
        client.setId(id);
        client.setName(name);
        client.setCpf(cpf);
        return client;
    }
}
