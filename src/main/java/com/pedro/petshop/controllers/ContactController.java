package com.pedro.petshop.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedro.petshop.entities.Contact;
import com.pedro.petshop.services.ContactService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

        private final ContactService contactService;

        public ContactController(ContactService contactService) {
                this.contactService = contactService;
        }

        @Operation(summary = "Create a new contact", description = "Creates a new contact record for a client")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Contact created successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PostMapping
        public Contact createContact(@RequestBody Contact contact) {
                return contactService.create(contact);
        }

        @Operation(summary = "Get contact by ID", description = "Retrieves a specific contact by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contact found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Contact not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Contact> getContactById(
                        @Parameter(description = "ID of the contact to be retrieved") @PathVariable Long id) {
                Optional<Contact> contact = contactService.findById(id);

                return contact.map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @Operation(summary = "Get all contacts", description = "Retrieves all contact records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of contacts returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @GetMapping
        public Page<Contact> getAllContacts(@Parameter(hidden = true) Pageable pageable) {
                return contactService.findAll(pageable);
        }

        @Operation(summary = "Update an existing contact", description = "Updates an existing contact record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Contact not found")
        })
        @PutMapping("/{id}")
        public Contact updateContact(
                        @Parameter(description = "ID of the contact to be updated") @PathVariable Long id,
                        @RequestBody Contact contact) {
                return contactService.update(id, contact);
        }

        @Operation(summary = "Delete a contact", description = "Deletes a contact record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contact deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Contact not found")
        })
        @DeleteMapping("/{id}")
        public boolean deleteContact(
                        @Parameter(description = "ID of the contact to be deleted") @PathVariable Long id) {
                return contactService.delete(id);
        }
}
