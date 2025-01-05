package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pedro.petshop.configs.CustomAuthentication;
import com.pedro.petshop.dtos.ContactDTO;
import com.pedro.petshop.entities.Contact;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.ContactMapper;
import com.pedro.petshop.services.ContactService;

@SpringBootTest
class ContactControllerTest {

    @Autowired
    private ContactController contactController;

    @Autowired
    private ContactMapper contactMapper;

    @MockitoBean
    private ContactService contactService;

    @Test
    void testGetAllContactsPaged() {
        ContactDTO contactDTO1 = createContact(1L, "Client1", "Phone", "123456789");
        ContactDTO contactDTO2 = createContact(2L, "Client2", "Email", "client2@example.com");
        Contact contact1 = contactMapper.toEntity(contactDTO1);
        Contact contact2 = contactMapper.toEntity(contactDTO2);

        Page<Contact> mockPage = new PageImpl<>(List.of(contact1, contact2), PageRequest.of(0, 10), 2);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        when(contactService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<ContactDTO> result = contactController.getAllContacts(pageable);
        assertEquals(2, result.getContent().size());
        assertEquals("Phone", result.getContent().get(0).getType());
        assertEquals("Email", result.getContent().get(1).getType());

        assertEquals(10, result.getSize());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testGetContactById_ContactExists() {
        ContactDTO mockContactDTO = createContact(1L, "Client1", "Phone", "123456789");
        Contact mockContact = contactMapper.toEntity(mockContactDTO);
        when(contactService.getByIdAndUserCpf(1L, "12345678900")).thenReturn(Optional.of(mockContact));

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.CLIENT.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<ContactDTO> response = contactController.getContactById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ContactDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Phone", body.getType());
    }

    @Test
    void testGetContactById_ContactNotFound() {
        when(contactService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ContactDTO> response = contactController.getContactById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateContact() {
        ContactDTO mockContactDTO = createContact(null, "Client1", "Phone", "123456789");
        Contact mockContact = contactMapper.toEntity(mockContactDTO);
        when(contactService.create(any(Contact.class))).thenReturn(mockContact);

        ContactDTO contactToCreate = createContact(null, "Client1", "Phone", "123456789");

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ResponseEntity<ContactDTO> result = contactController.createContact(contactToCreate);

        ContactDTO body = Optional.ofNullable(result.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));

        assertEquals("Phone", body.getType());
    }

    @Test
    void testUpdateContact() {
        ContactDTO mockContactDTO = createContact(1L, "Client1", "Phone", "123456789");
        Contact mockContact = contactMapper.toEntity(mockContactDTO);
        when(contactService.update(any(Long.class), any(Contact.class))).thenReturn(mockContact);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        ContactDTO contactToUpdate = createContact(1L, "Client1", "Phone", "123456789");

        ContactDTO result = contactController.updateContact(1L, contactToUpdate);

        assertEquals("Phone", result.getType());
    }

    @Test
    void testDeleteContact() {
        when(contactService.delete(1L)).thenReturn(true);

        CustomAuthentication customAuthentication = mock(CustomAuthentication.class);
        when(customAuthentication.getCpf()).thenReturn("12345678900");
        when(customAuthentication.getRole()).thenReturn(Role.ADMIN.toString());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        boolean result = contactController.deleteContact(1L);

        assertEquals(true, result);
    }

    private ContactDTO createContact(Long id, String client, String type, String value) {
        ContactDTO contact = new ContactDTO();
        contact.setId(id);
        contact.setType(type);
        contact.setValue(value);
        contact.setClientId(1L);
        return contact;
    }
}
