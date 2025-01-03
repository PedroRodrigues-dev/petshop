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

import com.pedro.petshop.configs.RolesAllowed;
import com.pedro.petshop.dtos.AddressDTO;
import com.pedro.petshop.entities.Address;
import com.pedro.petshop.mappers.AddressMapper;
import com.pedro.petshop.services.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

        private final AddressService addressService;
        private final AddressMapper addressMapper;

        public AddressController(AddressService addressService, AddressMapper addressMapper) {
                this.addressService = addressService;
                this.addressMapper = addressMapper;
        }

        @Operation(summary = "Create a new address", description = "Creates a new address record in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Address created successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @RolesAllowed({ "ADMIN" })
        @PostMapping
        public AddressDTO createAddress(@RequestBody AddressDTO address) {
                return addressMapper.toDto(addressService.create(addressMapper.toEntity(address)));
        }

        @Operation(summary = "Get address by ID", description = "Retrieves a specific address by its ID", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping("/{id}")
        public ResponseEntity<AddressDTO> getAddressById(
                        @Parameter(description = "ID of the address to be retrieved") @PathVariable Long id) {
                Optional<Address> address = addressService.findById(id);

                if (address.isPresent())
                        return ResponseEntity.ok(addressMapper.toDto(address.get()));

                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Get all addresses", description = "Retrieves all address records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of addresses returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping
        public Page<AddressDTO> getAllAddresses(@Parameter(hidden = true) Pageable pageable) {
                return addressMapper.pageToPageDTO(addressService.findAll(pageable));
        }

        @Operation(summary = "Update an existing address", description = "Updates an existing address record", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        @RolesAllowed({ "ADMIN" })
        @PutMapping("/{id}")
        public AddressDTO updateAddress(
                        @Parameter(description = "ID of the address to be updated") @PathVariable Long id,
                        @RequestBody AddressDTO address) {
                return addressMapper.toDto(addressService.update(id, addressMapper.toEntity(address)));
        }

        @Operation(summary = "Delete an address", description = "Deletes an address record by its ID", parameters = {
                        @Parameter(name = "id", description = "ID number", in = ParameterIn.PATH, example = "1") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        @RolesAllowed({ "ADMIN" })
        @DeleteMapping("/{id}")
        public boolean deleteAddress(
                        @Parameter(description = "ID of the address to be deleted") @PathVariable Long id) {
                return addressService.delete(id);
        }
}
