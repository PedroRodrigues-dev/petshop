package com.pedro.petshop.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedro.petshop.configs.CustomAuthentication;
import com.pedro.petshop.configs.RolesAllowed;
import com.pedro.petshop.dtos.ContactDTO;
import com.pedro.petshop.entities.Contact;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.ContactMapper;
import com.pedro.petshop.services.ClientService;
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
        private final ClientService clientService;
        private final ContactMapper contactMapper;

        public ContactController(ContactService contactService,
                        ClientService clientService, ContactMapper contactMapper) {
                this.contactService = contactService;
                this.clientService = clientService;
                this.contactMapper = contactMapper;
        }

        @Operation(summary = "Create a new contact", description = "Creates a new contact record for a client")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Contact created successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @RolesAllowed({ "ADMIN" })
        @PostMapping
        public ResponseEntity<ContactDTO> createContact(@RequestBody ContactDTO contact) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString())
                                        && !clientService.existsByIdAndCpf(contact.getClientId(), cpf))
                                return ResponseEntity.notFound().build();

                }

                return ResponseEntity
                                .ok(contactMapper.toDto(contactService.create(contactMapper.toEntity(contact))));
        }

        @Operation(summary = "Get contact by ID", description = "Retrieves a specific contact by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contact found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Contact not found")
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping("/{id}")
        public ResponseEntity<ContactDTO> getContactById(
                        @Parameter(description = "ID of the contact to be retrieved") @PathVariable("id") Long id) {
                Optional<Contact> contact = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                contact = contactService.getByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                contact = contactService.findById(id);

                }

                if (contact != null && contact.isPresent())
                        return ResponseEntity.ok(contactMapper.toDto(contact.get()));

                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Get all contacts", description = "Retrieves all contact records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of contacts returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping
        public Page<ContactDTO> getAllContacts(@Parameter(hidden = true) Pageable pageable) {
                Page<Contact> contacts = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                contacts = contactService.findAll(pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                contacts = contactService.getAllByUserCpf(cpf, pageable);

                }

                return contactMapper.pageToPageDTO(contacts);
        }

        @Operation(summary = "Update an existing contact", description = "Updates an existing contact record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Contact not found")
        })
        @RolesAllowed({ "ADMIN" })
        @PutMapping("/{id}")
        public ContactDTO updateContact(
                        @Parameter(description = "ID of the contact to be updated") @PathVariable("id") Long id,
                        @RequestBody ContactDTO contactDTO) {
                Contact contact = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                contact = contactService.updateByIdAndUserCpf(id, cpf,
                                                contactMapper.toEntity(contactDTO));
                        if (role.equals(Role.ADMIN.toString()))
                                contact = contactService.update(id, contactMapper.toEntity(contactDTO));

                }

                return contactMapper.toDto(contact);
        }

        @Operation(summary = "Delete a contact", description = "Deletes a contact record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contact deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Contact not found")
        })
        @RolesAllowed({ "ADMIN" })
        @DeleteMapping("/{id}")
        public boolean deleteContact(
                        @Parameter(description = "ID of the contact to be deleted") @PathVariable("id") Long id) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                return contactService.deleteByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                return contactService.delete(id);
                }

                return false;
        }
}
