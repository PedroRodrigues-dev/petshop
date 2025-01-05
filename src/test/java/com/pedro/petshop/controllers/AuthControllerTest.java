package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pedro.petshop.configs.JwtUtil;
import com.pedro.petshop.dtos.LoginDTO;
import com.pedro.petshop.dtos.RegisterDTO;
import com.pedro.petshop.dtos.TokenDTO;
import com.pedro.petshop.entities.User;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.UserMapper;
import com.pedro.petshop.services.UserService;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testRegister_Success() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("John Doe");
        registerDTO.setCpf("12345678900");
        registerDTO.setPassword("password123");

        User mockUser = new User();
        when(userMapper.registerToUser(registerDTO)).thenReturn(mockUser);
        when(userService.registerUser(mockUser)).thenReturn(true);

        HttpStatus response = authController.register(registerDTO);

        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void testRegister_Failure() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("John Doe");
        registerDTO.setCpf("12345678900");
        registerDTO.setPassword("password123");

        User mockUser = new User();
        when(userMapper.registerToUser(registerDTO)).thenReturn(mockUser);
        when(userService.registerUser(mockUser)).thenReturn(false);

        HttpStatus response = authController.register(registerDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response);
    }

    @Test
    void testLogin_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setName("John Doe");
        loginDTO.setPassword("password123");

        User mockUser = new User();
        mockUser.setRole(Role.CLIENT);
        mockUser.setCpf("12345678901");

        when(userService.loginUser(loginDTO)).thenReturn(Optional.of(mockUser));

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", mockUser.getRole());
        claims.put("cpf", mockUser.getCpf());

        String mockToken = "mocked.jwt.token";
        when(jwtUtil.generateToken(loginDTO.getName(), claims)).thenReturn(mockToken);

        ResponseEntity<TokenDTO> response = authController.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TokenDTO tokenDTO = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Bearer " + mockToken, tokenDTO.getToken());
    }

    @Test
    void testLogin_Failure() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setName("John Doe");
        loginDTO.setPassword("password123");

        when(userService.loginUser(loginDTO)).thenReturn(Optional.empty());

        ResponseEntity<TokenDTO> response = authController.login(loginDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
