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

import com.pedro.petshop.entities.Breed;
import com.pedro.petshop.services.BreedService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/breeds")
public class BreedController {

        private final BreedService breedService;

        public BreedController(BreedService breedService) {
                this.breedService = breedService;
        }

        @Operation(summary = "Create a new breed", description = "Creates a new breed record in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Breed created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @PostMapping
        public Breed createBreed(@RequestBody Breed breed) {
                return breedService.create(breed);
        }

        @Operation(summary = "Get breed by ID", description = "Retrieves a specific breed by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Breed found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Breed not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Breed> getBreedById(
                        @Parameter(description = "ID of the breed to be retrieved") @PathVariable Long id) {
                Optional<Breed> breed = breedService.findById(id);

                return breed.map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @Operation(summary = "Get all breeds", description = "Retrieves all breed records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of breeds returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @GetMapping
        public Page<Breed> getAllBreeds(@Parameter(hidden = true) Pageable pageable) {
                return breedService.findAll(pageable);
        }

        @Operation(summary = "Update an existing breed", description = "Updates an existing breed record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Breed updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Breed not found")
        })
        @PutMapping("/{id}")
        public Breed updateBreed(
                        @Parameter(description = "ID of the breed to be updated") @PathVariable Long id,
                        @RequestBody Breed breed) {
                return breedService.update(id, breed);
        }

        @Operation(summary = "Delete a breed", description = "Deletes a breed record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Breed deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Breed not found")
        })
        @DeleteMapping("/{id}")
        public boolean deleteBreed(@Parameter(description = "ID of the breed to be deleted") @PathVariable Long id) {
                return breedService.delete(id);
        }
}
