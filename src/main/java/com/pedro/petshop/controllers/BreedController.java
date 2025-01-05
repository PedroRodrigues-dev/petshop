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
import com.pedro.petshop.dtos.BreedDTO;
import com.pedro.petshop.entities.Breed;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.BreedMapper;
import com.pedro.petshop.services.BreedService;
import com.pedro.petshop.services.PetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/breeds")
public class BreedController {

        private final BreedService breedService;
        private final PetService petService;
        private final BreedMapper breedMapper;

        public BreedController(BreedService breedService, PetService petService, BreedMapper breedMapper) {
                this.breedService = breedService;
                this.petService = petService;
                this.breedMapper = breedMapper;
        }

        @Operation(summary = "Create a new breed", description = "Creates a new breed record in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Breed created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PostMapping
        public ResponseEntity<BreedDTO> createBreed(@RequestBody BreedDTO breed) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString())
                                        && !petService.existsByIdAndUserCpf(breed.getPetId(), cpf))
                                return ResponseEntity.notFound().build();

                }

                return ResponseEntity.ok(breedMapper.toDto(breedService.create(breedMapper.toEntity(breed))));
        }

        @Operation(summary = "Get breed by ID", description = "Retrieves a specific breed by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Breed found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Breed not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/{id}")
        public ResponseEntity<BreedDTO> getBreedById(
                        @Parameter(description = "ID of the breed to be retrieved") @PathVariable("id") Long id) {
                Optional<Breed> breed = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                breed = breedService.getByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                breed = breedService.findById(id);

                }

                if (breed != null && breed.isPresent())
                        return ResponseEntity.ok(breedMapper.toDto(breed.get()));

                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Get all breeds", description = "Retrieves all breed records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of breeds returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping
        public Page<BreedDTO> getAllBreeds(@Parameter(hidden = true) Pageable pageable) {
                Page<Breed> breeds = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                breeds = breedService.getAllByUserCpf(cpf, pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                breeds = breedService.findAll(pageable);
                }

                return breedMapper.pageToPageDTO(breeds);
        }

        @Operation(summary = "Get all breeds by client id", description = "Retrieves all breed by client id records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of breeds returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/client/{clientId}")
        public Page<BreedDTO> getAllBreedsByClientId(@PathVariable("clientId") Long clientId,
                        @Parameter(hidden = true) Pageable pageable) {
                Page<Breed> breeds = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                breeds = breedService.findAllByClientIdAndUserCpf(clientId, cpf, pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                breeds = breedService.findAllByClientId(clientId, pageable);
                }

                return breedMapper.pageToPageDTO(breeds);
        }

        @Operation(summary = "Get all breeds by pet id", description = "Retrieves all breed by pet id records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of breeds returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/pet/{petId}")
        public Page<BreedDTO> getAllBreedsByPetId(@PathVariable("petId") Long petId,
                        @Parameter(hidden = true) Pageable pageable) {
                Page<Breed> breeds = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                breeds = breedService.findAllByPetIdAndUserCpf(petId, cpf, pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                breeds = breedService.findAllByPetId(petId, pageable);
                }

                return breedMapper.pageToPageDTO(breeds);
        }

        @Operation(summary = "Update an existing breed", description = "Updates an existing breed record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Breed updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Breed not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PutMapping("/{id}")
        public BreedDTO updateBreed(
                        @Parameter(description = "ID of the breed to be updated") @PathVariable("id") Long id,
                        @RequestBody BreedDTO breedDTO) {
                Breed breed = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                breed = breedService.updateByIdAndUserCpf(id, cpf,
                                                breedMapper.toEntity(breedDTO));
                        if (role.equals(Role.ADMIN.toString()))
                                breed = breedService.update(id, breedMapper.toEntity(breedDTO));

                }

                return breedMapper.toDto(breed);
        }

        @Operation(summary = "Delete a breed", description = "Deletes a breed record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Breed deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Breed not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @DeleteMapping("/{id}")
        public boolean deleteBreed(
                        @Parameter(description = "ID of the breed to be deleted") @PathVariable("id") Long id) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                return breedService.deleteByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                return breedService.delete(id);
                }

                return false;
        }
}
