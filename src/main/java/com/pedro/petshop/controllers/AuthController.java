package com.pedro.petshop.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedro.petshop.configs.JwtUtil;
import com.pedro.petshop.dtos.LoginDTO;
import com.pedro.petshop.dtos.RegisterDTO;
import com.pedro.petshop.dtos.TokenDTO;
import com.pedro.petshop.entities.User;
import com.pedro.petshop.mappers.UserMapper;
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
    private UserMapper userMapper;

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
    public HttpStatus register(@RequestBody RegisterDTO register) {
        User user = userMapper.registerToUser(register);
        Boolean isRegistered = userService.registerUser(user);

        if (!isRegistered) {
            return HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.OK;
    }

    @Operation(summary = "Login", description = "Login in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginUser) {
        Optional<User> loggedUser = userService.loginUser(loginUser);

        if (loggedUser == null || loggedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", loggedUser.get().getRole());
        claims.put("cpf", loggedUser.get().getCpf());

        String token = jwtUtil.generateToken(loginUser.getName(), claims);
        TokenDTO tokenObject = new TokenDTO();
        tokenObject.setToken("Bearer " + token);

        return ResponseEntity.ok(tokenObject);
    }
}
