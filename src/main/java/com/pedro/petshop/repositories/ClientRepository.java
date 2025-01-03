package com.pedro.petshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
