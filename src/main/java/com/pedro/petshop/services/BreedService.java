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

    public Page<Breed> findAllByClientId(Long clientId, Pageable pageable) {
        return breedRepository.findAllByClientId(clientId, pageable);
    }

    public Page<Breed> findAllByClientIdAndUserCpf(Long clientId, String cpf, Pageable pageable) {
        return breedRepository.findAllByClientIdAndUserCpf(clientId, cpf, pageable);
    }

    public Page<Breed> findAllByPetId(Long petId, Pageable pageable) {
        return breedRepository.findAllByPetId(petId, pageable);
    }

    public Page<Breed> findAllByPetIdAndUserCpf(Long petId, String cpf, Pageable pageable) {
        return breedRepository.findAllByPetIdAndUserCpf(petId, cpf, pageable);
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

    public boolean existsByIdAndUserCpf(Long id, String cpf) {
        return breedRepository.existsByIdAndUserCpf(id, cpf);
    }

    public Optional<Breed> getByIdAndUserCpf(Long id, String cpf) {
        return breedRepository.findByIdAndUserCpf(id, cpf);
    }

    public Page<Breed> getAllByUserCpf(String cpf, Pageable pageable) {
        return breedRepository.findAllByUserCpf(cpf, pageable);
    }

    public Breed updateByIdAndUserCpf(Long id, String cpf, Breed updatedBreed) {
        if (breedRepository.existsByIdAndUserCpf(id, cpf)) {
            updatedBreed.setId(id);
            return breedRepository.save(updatedBreed);
        } else {
            throw new RuntimeException("Breed not found with id and cpf");
        }
    }

    public boolean deleteByIdAndUserCpf(Long id, String cpf) {
        if (breedRepository.existsByIdAndUserCpf(id, cpf)) {
            breedRepository.deleteByIdAndUserCpf(id, cpf);
            return true;
        }
        return false;
    }
}
