package com.pedro.petshop.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByName(String name);
}
