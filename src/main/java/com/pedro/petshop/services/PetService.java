package com.pedro.petshop.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pedro.petshop.entities.Pet;
import com.pedro.petshop.repositories.PetRepository;

@Service
public class PetService {

    @Value("${upload.path}")
    private String uploadPath;

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Optional<Resource> getProfileImage(Long id) {
        Pet pet = petRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));

        if (pet.getImage() == null)
            return null;

        try {
            Path filePath = Paths.get(uploadPath).resolve(pet.getImage()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return null;
            }

            return Optional.of(resource);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean uploadImage(Long id, MultipartFile file) {
        Optional<Pet> pet = petRepository.findById(id);
        if (!pet.isPresent())
            return false;
        try {
            Path directoryPath = Paths.get(uploadPath);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            String filename = "pet_" + id + "_" + file.getOriginalFilename();
            Path filePath = directoryPath.resolve(filename);
            file.transferTo(filePath);

            pet.get().setImage(filename);
            petRepository.save(pet.get());

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Pet create(Pet pet) {
        return petRepository.save(pet);
    }

    public Optional<Pet> findById(Long id) {
        return petRepository.findById(id);
    }

    public Page<Pet> findAll(Pageable pageable) {
        return petRepository.findAll(pageable);
    }

    public Pet update(Long id, Pet pet) {
        if (petRepository.existsById(id)) {
            return petRepository.save(pet);
        }
        return null;
    }

    public boolean delete(Long id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
