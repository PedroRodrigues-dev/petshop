package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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

import com.pedro.petshop.entities.User;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.services.UserService;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockitoBean
    private UserService userService;

    @Test
    void testCreateUser() {
        User mockUser = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        when(userService.create(any(User.class))).thenReturn(mockUser);

        User userToCreate = createUser("12345678901", "Pedro", Role.ADMIN, "password");

        User result = userController.createUser(userToCreate);

        assertEquals("Pedro", result.getName());
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals("12345678901", result.getCpf());
    }

    @Test
    void testGetUserById_UserExists() {
        User mockUser = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        when(userService.findById("12345678901")).thenReturn(Optional.of(mockUser));

        ResponseEntity<User> response = userController.getUserById("12345678901");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        User body = response.getBody();
        assertEquals("Pedro", body.getName());
        assertEquals(Role.ADMIN, body.getRole());
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userService.findById("12345678901")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById("12345678901");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllUsers() {
        User mockUser1 = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        User mockUser2 = createUser("98765432100", "Maria", Role.CLIENT, "password");
        Page<User> mockPage = new PageImpl<>(Arrays.asList(mockUser1, mockUser2), PageRequest.of(0, 10), 2);

        when(userService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> result = userController.getAllUsers(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("Pedro", result.getContent().get(0).getName());
        assertEquals("Maria", result.getContent().get(1).getName());
    }

    @Test
    void testUpdateUser() {
        User mockUser = createUser("12345678901", "Pedro", Role.ADMIN, "password");
        when(userService.update(any(String.class), any(User.class))).thenReturn(mockUser);

        User userToUpdate = createUser("12345678901", "Pedro", Role.ADMIN, "password");

        User result = userController.updateUser("12345678901", userToUpdate);

        assertEquals("Pedro", result.getName());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void testDeleteUser() {
        when(userService.delete("12345678901")).thenReturn(true);

        boolean result = userController.deleteUser("12345678901");

        assertEquals(true, result);
    }

    private User createUser(String cpf, String name, Role role, String password) {
        User user = new User();
        user.setCpf(cpf);
        user.setName(name);
        user.setRole(role);
        user.setPassword(password);
        return user;
    }
}
