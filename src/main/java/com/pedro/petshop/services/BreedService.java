package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pedro.petshop.entities.Breed;
import com.pedro.petshop.repositories.BreedRepository;

@Service
public class BreedService {

    private final BreedRepository breedRepository;

    public BreedService(BreedRepository breedRepository) {
        this.breedRepository = breedRepository;
    }

    public Breed create(Breed breed) {
        return breedRepository.save(breed);
    }

    public Optional<Breed> findById(Long id) {
        return breedRepository.findById(id);
    }

    public Page<Breed> findAll(Pageable pageable) {
        return breedRepository.findAll(pageable);
    }

    public Breed update(Long id, Breed breed) {
        if (breedRepository.existsById(id)) {
            return breedRepository.save(breed);
        }
        return null;
    }

    public boolean delete(Long id) {
        if (breedRepository.existsById(id)) {
            breedRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
