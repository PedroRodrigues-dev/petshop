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
import com.pedro.petshop.dtos.AddressDTO;
import com.pedro.petshop.entities.Address;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.AddressMapper;
import com.pedro.petshop.services.AddressService;
import com.pedro.petshop.services.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

        private final AddressService addressService;
        private final ClientService clientService;
        private final AddressMapper addressMapper;

        public AddressController(AddressService addressService, AddressMapper addressMapper,
                        ClientService clientService) {
                this.addressService = addressService;
                this.clientService = clientService;
                this.addressMapper = addressMapper;
        }

        @Operation(summary = "Create a new address", description = "Creates a new address record in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Address created successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PostMapping
        public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO address) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString())
                                        && !clientService.existsByIdAndCpf(address.getClientId(), cpf))
                                return ResponseEntity.notFound().build();

                }

                return ResponseEntity
                                .ok(addressMapper.toDto(addressService.create(addressMapper.toEntity(address))));
        }

        @Operation(summary = "Get address by ID", description = "Retrieves a specific address by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/{id}")
        public ResponseEntity<AddressDTO> getAddressById(
                        @Parameter(description = "ID of the address to be retrieved") @PathVariable("id") Long id) {
                Optional<Address> address = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                address = addressService.getByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                address = addressService.findById(id);

                }

                if (address != null && address.isPresent())
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
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping
        public Page<AddressDTO> getAllAddresses(@Parameter(hidden = true) Pageable pageable) {
                Page<Address> address = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                address = addressService.getAllByUserCpf(cpf, pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                address = addressService.findAll(pageable);
                }

                return addressMapper.pageToPageDTO(address);
        }

        @Operation(summary = "Get addresses by clientId", description = "Retrieves address by clientId records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10")
        })
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of addressesreturned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"), })

        @RolesAllowed({ "ADMIN", "CLIENT" })
        @GetMapping("/client/{clientId}")
        public Page<AddressDTO> getAddressesByClientId(@PathVariable("clientId") Long clientId,
                        @Parameter(hidden = true) Pageable pageable) {
                Page<Address> address = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                address = addressService.findAllByClientIdAndUserCpf(clientId, cpf,
                                                pageable);
                        if (role.equals(Role.ADMIN.toString()))
                                address = addressService.findAllByClientId(clientId, pageable);
                }

                return addressMapper.pageToPageDTO(address);
        }

        @Operation(summary = "Update an existing address", description = "Updates an existing address record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @PutMapping("/{id}")
        public AddressDTO updateAddress(
                        @Parameter(description = "ID of the address to be updated") @PathVariable("id") Long id,
                        @RequestBody AddressDTO addressDTO) {
                Address address = null;

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                address = addressService.updateByIdAndUserCpf(id, cpf,
                                                addressMapper.toEntity(addressDTO));
                        if (role.equals(Role.ADMIN.toString()))
                                address = addressService.update(id, addressMapper.toEntity(addressDTO));

                }

                return addressMapper.toDto(address);
        }

        @Operation(summary = "Delete an address", description = "Deletes an address record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Address deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Address not found")
        })
        @RolesAllowed({ "ADMIN", "CLIENT" })
        @DeleteMapping("/{id}")
        public boolean deleteAddress(
                        @Parameter(description = "ID of the address to be deleted") @PathVariable("id") Long id) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof CustomAuthentication) {
                        CustomAuthentication customAuth = (CustomAuthentication) authentication;
                        String role = customAuth.getRole();
                        String cpf = customAuth.getCpf();

                        if (role.equals(Role.CLIENT.toString()))
                                return addressService.deleteByIdAndUserCpf(id, cpf);
                        if (role.equals(Role.ADMIN.toString()))
                                return addressService.delete(id);
                }

                return false;
        }
}
