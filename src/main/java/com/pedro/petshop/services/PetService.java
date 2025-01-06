package com.pedro.petshop.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pedro.petshop.configs.Tool;
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
        Pet pet = petRepository.findById(id).orElseThrow(() -> new RuntimeException("Pet not found"));

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

            String oldImage = pet.get().getImage();
            if (oldImage != null && !oldImage.isEmpty()) {
                Path oldImagePath = directoryPath.resolve(oldImage);
                if (Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                }
            }

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

    public Page<Pet> findAllByClientId(Long clientId, Pageable pageable) {
        return petRepository.findAllByClientId(clientId, pageable);
    }

    public Page<Pet> findAllByClientIdAndUserCpf(Long clientId, String cpf,
            Pageable pageable) {
        return petRepository.findAllByClientIdAndUserCpf(clientId, cpf, pageable);
    }

    public Pet update(Long id, Pet pet) {
        pet.setId(id);
        return petRepository.findById(id).map(existingPet -> {
            BeanUtils.copyProperties(pet, existingPet, Tool.getNullPropertyNames(pet));
            return petRepository.save(existingPet);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByIdAndUserCpf(Long id, String cpf) {
        return petRepository.existsByIdAndUserCpf(id, cpf);
    }

    public Optional<Pet> getByIdAndUserCpf(Long id, String cpf) {
        return petRepository.findByIdAndUserCpf(id, cpf);
    }

    public Page<Pet> getAllByUserCpf(String cpf, Pageable pageable) {
        return petRepository.findAllByUserCpf(cpf, pageable);
    }

    public Pet updateByIdAndUserCpf(Long id, String cpf, Pet updatedPet) {
        updatedPet.setId(id);
        return petRepository.findByIdAndUserCpf(id, cpf).map(existingPet -> {
            BeanUtils.copyProperties(updatedPet, existingPet, Tool.getNullPropertyNames(updatedPet));
            return petRepository.save(existingPet);
        }).orElse(null);
    }

    public boolean deleteByIdAndUserCpf(Long id, String cpf) {
        if (petRepository.existsByIdAndUserCpf(id, cpf)) {
            petRepository.deleteByIdAndUserCpf(id, cpf);
            return true;
        }
        return false;
    }
}
