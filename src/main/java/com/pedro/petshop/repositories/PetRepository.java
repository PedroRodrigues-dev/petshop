package com.pedro.petshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}
