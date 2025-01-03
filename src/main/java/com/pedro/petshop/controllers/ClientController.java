package com.pedro.petshop.controllers;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pedro.petshop.configs.RolesAllowed;
import com.pedro.petshop.dtos.ClientDTO;
import com.pedro.petshop.entities.Client;
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

        @Operation(summary = "Upload a client image", description = "Upload a client image in the system", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @RolesAllowed({ "ADMIN" })
        @PostMapping("/{id}/upload-image")
        public HttpStatus uploadProfileImage(@PathVariable Long id,
                        @RequestParam("file") MultipartFile file) {
                boolean isSaved = clientService.uploadImage(id, file);
                if (isSaved)
                        return HttpStatus.OK;

                return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        @Operation(summary = "Download a client image", description = "Download a client image from the system", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image downloaded successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Not found"),
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping("/{id}/download-image")
        public ResponseEntity<Resource> getProfileImage(@PathVariable Long id) {
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
        @RolesAllowed({ "ADMIN" })
        @PostMapping
        public ClientDTO createClient(@RequestBody ClientDTO client) {
                return clientMapper.toDto(clientService.create(clientMapper.toEntity(client)));
        }

        @Operation(summary = "Get client by ID", description = "Retrieves a specific client by its ID", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Client not found")
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping("/{id}")
        public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
                Optional<Client> client = clientService.findById(id);

                if (client.isPresent())
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
        @RolesAllowed({ "ADMIN" })
        @GetMapping
        public Page<ClientDTO> getAllClients(@Parameter(hidden = true) Pageable pageable) {
                return clientMapper.pageToPageDTO(clientService.findAll(pageable));
        }

        @Operation(summary = "Update an existing client", description = "Updates an existing client record", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Client not found")
        })
        @RolesAllowed({ "ADMIN" })
        @PutMapping("/{id}")
        public ClientDTO updateClient(
                        @Parameter(description = "ID of the client to be updated") @PathVariable Long id,
                        @RequestBody ClientDTO client) {
                return clientMapper.toDto(clientService.update(id, clientMapper.toEntity(client)));
        }

        @Operation(summary = "Delete a client", description = "Deletes a client record by its ID", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Client not found")
        })
        @RolesAllowed({ "ADMIN" })
        @DeleteMapping("/{id}")
        public boolean deleteClient(@Parameter(description = "ID of the client to be deleted") @PathVariable Long id) {
                return clientService.delete(id);
        }
}
