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

import com.pedro.petshop.entities.Pet;
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

        public PetController(PetService petService) {
                this.petService = petService;
        }

        @Operation(summary = "Upload a pet image", description = "Upload a pet image in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping("/{id}/upload-image")
        public HttpStatus uploadProfileImage(@PathVariable Long id,
                        @RequestParam("file") MultipartFile file) {
                boolean isSaved = petService.uploadImage(id, file);
                if (isSaved)
                        return HttpStatus.OK;

                return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        @Operation(summary = "Download a pet image", description = "Download a pet image from the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Image downloaded successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Not found"),
        })
        @GetMapping("/{id}/download-image")
        public ResponseEntity<Resource> getProfileImage(@PathVariable Long id) {
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
        @PostMapping
        public Pet createPet(@RequestBody Pet pet) {
                return petService.create(pet);
        }

        @Operation(summary = "Get pet by ID", description = "Retrieves a specific pet by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pet found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Pet not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Pet> getPetById(
                        @Parameter(description = "ID of the pet to be retrieved") @PathVariable Long id) {
                Optional<Pet> pet = petService.findById(id);

                return pet.map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @Operation(summary = "Get all pets", description = "Retrieves all pet records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of pets returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @GetMapping
        public Page<Pet> getAllPets(@Parameter(hidden = true) Pageable pageable) {
                return petService.findAll(pageable);
        }

        @Operation(summary = "Update an existing pet", description = "Updates an existing pet record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pet updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Pet not found")
        })
        @PutMapping("/{id}")
        public Pet updatePet(
                        @Parameter(description = "ID of the pet to be updated") @PathVariable Long id,
                        @RequestBody Pet pet) {
                return petService.update(id, pet);
        }

        @Operation(summary = "Delete a pet", description = "Deletes a pet record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pet deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Pet not found")
        })
        @DeleteMapping("/{id}")
        public boolean deletePet(@Parameter(description = "ID of the pet to be deleted") @PathVariable Long id) {
                return petService.delete(id);
        }
}