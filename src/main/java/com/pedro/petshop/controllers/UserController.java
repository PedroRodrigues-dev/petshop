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

import com.pedro.petshop.entities.User;
import com.pedro.petshop.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @Operation(summary = "Create a new user", description = "Creates a new user in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid user input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @PostMapping
        public User createUser(@RequestBody User user) {
                return userService.create(user);
        }

        @Operation(summary = "Get user by CPF", description = "Retrieves a specific user by their CPF")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @GetMapping("/{cpf}")
        public ResponseEntity<User> getUserById(
                        @Parameter(description = "CPF of the user to be retrieved") @PathVariable String cpf) {
                Optional<User> user = userService.findById(cpf);

                return user.map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @Operation(summary = "Get all users", description = "Retrieves all users from the system", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of users returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @GetMapping
        public Page<User> getAllUsers(@Parameter(hidden = true) Pageable pageable) {
                return userService.findAll(pageable);
        }

        @Operation(summary = "Update an existing user", description = "Updates an existing user's details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid user input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PutMapping("/{cpf}")
        public User updateUser(
                        @Parameter(description = "CPF of the user to be updated") @PathVariable String cpf,
                        @RequestBody User user) {
                return userService.update(cpf, user);
        }

        @Operation(summary = "Delete a user", description = "Deletes a user record by their CPF")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @DeleteMapping("/{cpf}")
        public boolean deleteUser(@Parameter(description = "CPF of the user to be deleted") @PathVariable String cpf) {
                return userService.delete(cpf);
        }
}
