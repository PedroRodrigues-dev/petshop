package com.pedro.petshop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedro.petshop.configs.JwtUtil;
import com.pedro.petshop.entities.Login;
import com.pedro.petshop.entities.Token;
import com.pedro.petshop.entities.User;
import com.pedro.petshop.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Register", description = "Register in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registered successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String result = userService.registerUser(user);

        if (result.equals("Usuário já existe.")) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Login", description = "Login in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody Login loginUser) {
        Boolean isLoggged = userService.loginUser(loginUser);

        if (!isLoggged) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String token = jwtUtil.generateToken(loginUser.getName());
        Token tokenObject = new Token();
        tokenObject.setToken("Bearer " + token);

        return ResponseEntity.ok(tokenObject);
    }
}
