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
import com.pedro.petshop.entities.Contact;
import com.pedro.petshop.services.ContactService;

@SpringBootTest
class ContactControllerTest {

    @Autowired
    private ContactController contactController;

    @MockitoBean
    private ContactService contactService;

    @Test
    void testGetAllContactsPaged() {
        List<Contact> mockContacts = Arrays.asList(
                createContact(1L, "Client1", "Phone", "123456789"),
                createContact(2L, "Client2", "Email", "client2@example.com"));

        Page<Contact> mockPage = new PageImpl<>(mockContacts, PageRequest.of(0, 10), mockContacts.size());

        when(contactService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Contact> result = contactController.getAllContacts(pageable);
        assertEquals(2, result.getContent().size());
        assertEquals("Phone", result.getContent().get(0).getType());
        assertEquals("Email", result.getContent().get(1).getType());

        assertEquals(10, result.getSize());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testGetContactById_ContactExists() {
        Contact mockContact = createContact(1L, "Client1", "Phone", "123456789");
        when(contactService.findById(1L)).thenReturn(Optional.of(mockContact));

        ResponseEntity<Contact> response = contactController.getContactById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Contact body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Phone", body.getType());
    }

    @Test
    void testGetContactById_ContactNotFound() {
        when(contactService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Contact> response = contactController.getContactById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateContact() {
        Contact mockContact = createContact(null, "Client1", "Phone", "123456789");
        when(contactService.create(any(Contact.class))).thenReturn(mockContact);

        Contact contactToCreate = createContact(null, "Client1", "Phone", "123456789");

        Contact result = contactController.createContact(contactToCreate);

        assertEquals("Phone", result.getType());
    }

    @Test
    void testUpdateContact() {
        Contact mockContact = createContact(1L, "Client1", "Phone", "123456789");
        when(contactService.update(any(Long.class), any(Contact.class))).thenReturn(mockContact);

        Contact contactToUpdate = createContact(1L, "Client1", "Phone", "123456789");

        Contact result = contactController.updateContact(1L, contactToUpdate);

        assertEquals("Phone", result.getType());
    }

    @Test
    void testDeleteContact() {
        when(contactService.delete(1L)).thenReturn(true);

        boolean result = contactController.deleteContact(1L);

        assertEquals(true, result);
    }

    private Contact createContact(Long id, String client, String type, String value) {
        Client clientObject = new Client();
        clientObject.setName(client);

        Contact contact = new Contact();
        contact.setId(id);
        contact.setClient(clientObject);
        contact.setType(type);
        contact.setValue(value);
        return contact;
    }
}
