package com.pedro.petshop.controllers;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pedro.petshop.configs.CustomAuthentication;
import com.pedro.petshop.configs.RolesAllowed;
import com.pedro.petshop.dtos.ClientDTO;
import com.pedro.petshop.entities.Client;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.ClientMapper;
import com.pedro.petshop.services.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

        private final ClientService clientService;
        private final ClientMapper clientMapper;

        public ClientController(ClientService clientService, ClientMapper clientMapper) {
                this.clientService = clientService;
                this.clientMapper = clientMapper;
        }

        @Operation(summary = "Upload a client image", description = "Upload a client image in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public HttpStatus uploadProfileImage(@PathVariable("id") Long id,
                        @RequestPart(value = "file", required = true) MultipartFile file) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()) && !clientService.existsByIdAndCpf(id, cpf))
                                return HttpStatus.NOT_FOUND;
                }

                boolean isSaved = clientService.uploadImage(id, file);
                if (isSaved)
                        return HttpStatus.OK;

                return HttpStatus.NOT_FOUND;
        }

        @Operation(summary = "Download a client image", description = "Download a client image from the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image downloaded successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Not found"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/{id}/download-image")
        public ResponseEntity<Resource> getProfileImage(@PathVariable("id") Long id) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()) && !clientService.existsByIdAndCpf(id, cpf))
                                return ResponseEntity.notFound().build();
                }

                Optional<Resource> resource = clientService.getProfileImage(id);
                if (!resource.isPresent())
                        return ResponseEntity.notFound().build();

                return ResponseEntity.ok()
                                .contentType(MediaType.IMAGE_JPEG)
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"" + resource.get().getFilename() + "\"")
                                .body(resource.get());
        }

        @Operation(summary = "Create a new client", description = "Creates a new client record in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Client created successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PostMapping
        public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO client) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString())) {
                                client.setCpf(cpf);
                        }
                }

                return ResponseEntity
                                .ok(clientMapper.toDto(clientService.create(clientMapper.toEntity(client))));
        }

        @Operation(summary = "Get client by ID", description = "Retrieves a specific client by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Client not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/{id}")
        public ResponseEntity<ClientDTO> getClientById(@PathVariable("id") Long id) {
                Optional<Client> client = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                client = clientService.getByIdAndCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                client = clientService.findById(id);

                }

                if (client != null && client.isPresent())
                        return ResponseEntity.ok(clientMapper.toDto(client.get()));

                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Get all clients", description = "Retrieves all client records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of clients returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping
        public Page<ClientDTO> getAllClients(@Parameter(hidden = true) Pageable pageable) {
                Page<Client> clients = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                clients = clientService.findAll(pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                clients = clientService.getAllByCpf(cpf, pageable);

                }

                return clientMapper.pageToPageDTO(clients);
        }

        @Operation(summary = "Update an existing client", description = "Updates an existing client record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Client not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PutMapping("/{id}")
        public ClientDTO updateClient(
                        @Parameter(description = "ID of the client to be updated") @PathVariable("id") Long id,
                        @RequestBody ClientDTO clientDTO) {
                Client client = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                client = clientService.updateByIdAndCpf(id, cpf, clientMapper.toEntity(clientDTO));
                        if (role.equals(Role.ADMIN.toString()))
                                client = clientService.update(id, clientMapper.toEntity(clientDTO));

                }

                return clientMapper.toDto(client);
        }

        @Operation(summary = "Delete a client", description = "Deletes a client record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Client not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @DeleteMapping("/{id}")
        public boolean deleteClient(
                        @Parameter(description = "ID of the client to be deleted") @PathVariable("id") Long id) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                return clientService.deleteByIdAndCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                return clientService.delete(id);
                }

                return false;
        }
}
