package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pedro.petshop.dtos.UserDTO;
import com.pedro.petshop.entities.User;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.mappers.UserMapper;
import com.pedro.petshop.services.UserService;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserMapper userMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void testCreateUser() {
        UserDTO mockUserDTO = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        User mockUser = userMapper.toEntity(mockUserDTO);
        when(userService.create(any(User.class))).thenReturn(mockUser);

        UserDTO userToCreate = createUser("12345678901", "Pedro", Role.ADMIN, "password");

        UserDTO result = userController.createUser(userToCreate);

        assertEquals("Pedro", result.getName());
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals("12345678901", result.getCpf());
    }

    @Test
    void testGetUserById_UserExists() {
        UserDTO mockUserDTO = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        User mockUser = userMapper.toEntity(mockUserDTO);
        when(userService.findById("12345678901")).thenReturn(Optional.of(mockUser));

        ResponseEntity<UserDTO> response = userController.getUserById("12345678901");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Pedro", body.getName());
        assertEquals(Role.ADMIN, body.getRole());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userService.findById("12345678901")).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = userController.getUserById("12345678901");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllUsers() {
        UserDTO userDTO1 = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        UserDTO userDTO2 = createUser("98765432100", "Maria", Role.CLIENT, "password");
        User user1 = userMapper.toEntity(userDTO1);
        User user2 = userMapper.toEntity(userDTO2);

        Page<User> mockPage = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 10), 2);

        when(userService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> result = userController.getAllUsers(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Pedro", result.getContent().get(0).getName());
        assertEquals("Maria", result.getContent().get(1).getName());
    }

    @Test
    void testUpdateUser() {
        UserDTO mockUserDTO = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        User mockUser = userMapper.toEntity(mockUserDTO);
        when(userService.update(any(String.class), any(User.class))).thenReturn(mockUser);

        UserDTO userToUpdate = createUser("12345678901", "Pedro", Role.ADMIN, "password");

        UserDTO result = userController.updateUser("12345678901", userToUpdate);

        assertEquals("Pedro", result.getName());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void testDeleteUser() {
        when(userService.delete("12345678901")).thenReturn(true);

        boolean result = userController.deleteUser("12345678901");

        assertEquals(true, result);
    }

    private UserDTO createUser(String cpf, String name, Role role, String password) {
        UserDTO user = new UserDTO();
        user.setCpf(cpf);
        user.setName(name);
        user.setRole(role);
        user.setPassword(password);
        return user;
    }
}
