package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pedro.petshop.dtos.LoginDTO;
import com.pedro.petshop.entities.User;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public Optional<User> findById(String cpf) {
        return userRepository.findById(cpf);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User update(String cpf, User user) {
        if (userRepository.existsById(cpf)) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            return userRepository.save(user);
        }
        return null;
    }

    public boolean delete(String cpf) {
        if (userRepository.existsById(cpf)) {
            userRepository.deleteById(cpf);
            return true;
        }
        return false;
    }

    public Boolean registerUser(User user) {
        if (userRepository.existsById(user.getCpf())) {
            return false;
        }

        user.setRole(Role.CLIENT);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);
        return true;
    }

    public Boolean loginUser(LoginDTO loginUser) {
        Optional<User> userOptional = userRepository.findByName(loginUser.getName());

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }
}
