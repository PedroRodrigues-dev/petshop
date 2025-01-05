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
import com.pedro.petshop.dtos.PetDTO;
import com.pedro.petshop.entities.Pet;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.PetMapper;
import com.pedro.petshop.services.ClientService;
import com.pedro.petshop.services.PetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/pets")
public class PetController {

        private final PetService petService;
        private final ClientService clientService;
        private final PetMapper petMapper;

        public PetController(PetService petService, ClientService clientService, PetMapper petMapper) {
                this.petService = petService;
                this.clientService = clientService;
                this.petMapper = petMapper;
        }

        @Operation(summary = "Upload a pet image", description = "Upload a pet image in the system")
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

                        if (role.equals(Role.CLIENT.toString()) && !petService.existsByIdAndUserCpf(id, cpf))
                                return HttpStatus.NOT_FOUND;
                }

                boolean isSaved = petService.uploadImage(id, file);
                if (isSaved)
                        return HttpStatus.OK;

                return HttpStatus.NOT_FOUND;
        }

        @Operation(summary = "Download a pet image", description = "Download a pet image from the system")
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

                        if (role.equals(Role.CLIENT.toString()) && !petService.existsByIdAndUserCpf(id, cpf))
                                return ResponseEntity.notFound().build();
                }

                Optional<Resource> resource = petService.getProfileImage(id);
                if (!resource.isPresent())
                        return ResponseEntity.notFound().build();

                return ResponseEntity.ok()
                                .contentType(MediaType.IMAGE_JPEG)
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"" + resource.get().getFilename() + "\"")
                                .body(resource.get());
        }

        @Operation(summary = "Create a new pet", description = "Creates a new pet record for a client")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Pet created successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PostMapping
        public ResponseEntity<PetDTO> createPet(@RequestBody PetDTO pet) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString())
                                        && !clientService.existsByIdAndCpf(pet.getClientId(), cpf))
                                return ResponseEntity.notFound().build();

                }

                return ResponseEntity
                                .ok(petMapper.toDto(petService.create(petMapper.toEntity(pet))));
        }

        @Operation(summary = "Get pet by ID", description = "Retrieves a specific pet by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pet found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Pet not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/{id}")
        public ResponseEntity<PetDTO> getPetById(
                        @Parameter(description = "ID of the pet to be retrieved") @PathVariable("id") Long id) {
                Optional<Pet> pet = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                pet = petService.getByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                pet = petService.findById(id);

                }

                if (pet != null && pet.isPresent())
                        return ResponseEntity.ok(petMapper.toDto(pet.get()));

                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Get all pets", description = "Retrieves all pet records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of pets returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping
        public Page<PetDTO> getAllPets(@Parameter(hidden = true) Pageable pageable) {
                Page<Pet> pets = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                pets = petService.getAllByUserCpf(cpf, pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                pets = petService.findAll(pageable);
                }

                return petMapper.pageToPageDTO(pets);
        }

        @Operation(summary = "Get pets by clientId", description = "Retrieves pet byclientId records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10")
        })

        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of pets returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/client/{clientId}")
        public Page<PetDTO> getPetsByClientId(@PathVariable("clientId") Long clientId,
                        @Parameter(hidden = true) Pageable pageable) {
                Page<Pet> pets = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                pets = petService.findAllByClientIdAndUserCpf(clientId, cpf, pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                pets = petService.findAllByClientId(clientId, pageable);
                }

                return petMapper.pageToPageDTO(pets);
        }

        @Operation(summary = "Update an existing pet", description = "Updates an existing pet record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pet updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Pet not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PutMapping("/{id}")
        public PetDTO updatePet(
                        @Parameter(description = "ID of the pet to be updated") @PathVariable("id") Long id,
                        @RequestBody PetDTO petDTO) {
                Pet pet = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                pet = petService.updateByIdAndUserCpf(id, cpf, petMapper.toEntity(petDTO));
                        if (role.equals(Role.ADMIN.toString()))
                                pet = petService.update(id, petMapper.toEntity(petDTO));

                }

                return petMapper.toDto(pet);
        }

        @Operation(summary = "Delete a pet", description = "Deletes a pet record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pet deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Pet not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @DeleteMapping("/{id}")
        public boolean deletePet(@Parameter(description = "ID of the pet to be deleted") @PathVariable("id") Long id) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                return petService.deleteByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                return petService.delete(id);
                }

                return false;
        }
}
