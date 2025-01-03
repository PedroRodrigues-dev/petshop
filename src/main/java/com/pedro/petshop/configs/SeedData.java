package com.pedro.petshop.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pedro.petshop.entities.User;
import com.pedro.petshop.enums.Role;
import com.pedro.petshop.repositories.UserRepository;

@Configuration
public class SeedData {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository) {
        return args -> {
            String adminCpf = "12345678900";
            if (!userRepository.existsById(adminCpf)) {
                User admin = new User();
                admin.setCpf(adminCpf);
                admin.setName("admin");
                admin.setRole(Role.ADMIN);
                String hashedPassword = passwordEncoder.encode("admin123");
                admin.setPassword(hashedPassword);

                userRepository.save(admin);
                System.out.println("Usuário ADMIN criado com sucesso!");
            }
        };
    }
}
