package com.pedro.petshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Breed;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {

}
