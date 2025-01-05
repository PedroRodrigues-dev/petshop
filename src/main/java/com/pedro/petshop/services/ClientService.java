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

import com.pedro.petshop.entities.Client;
import com.pedro.petshop.repositories.ClientRepository;

@Service
public class ClientService {

    @Value("${upload.path}")
    private String uploadPath;

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<Resource> getProfileImage(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));

        if (client.getImage() == null)
            return null;

        try {
            Path filePath = Paths.get(uploadPath).resolve(client.getImage()).normalize();
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
        Optional<Client> client = clientRepository.findById(id);
        if (!client.isPresent())
            return false;
        try {
            Path directoryPath = Paths.get(uploadPath);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            String filename = "client_" + id + "_" + file.getOriginalFilename();
            Path filePath = directoryPath.resolve(filename);

            String oldImage = client.get().getImage();
            if (oldImage != null && !oldImage.isEmpty()) {
                Path oldImagePath = directoryPath.resolve(oldImage);
                if (Files.exists(oldImagePath)) {
                    Files.delete(oldImagePath);
                }
            }

            file.transferTo(filePath);

            client.get().setImage(filename);
            clientRepository.save(client.get());

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Client create(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Client update(Long id, Client client) {
        if (clientRepository.existsById(id)) {
            return clientRepository.save(client);
        }
        return null;
    }

    public boolean delete(Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByIdAndCpf(Long id, String cpf) {
        return clientRepository.existsByIdAndCpf(id, cpf);
    }

    public Optional<Client> getByIdAndCpf(Long id, String cpf) {
        return clientRepository.findByIdAndCpf(id, cpf);
    }

    public Page<Client> getAllByCpf(String cpf, Pageable pageable) {
        return clientRepository.findAllByCpf(cpf, pageable);
    }

    public Client updateByIdAndCpf(Long id, String cpf, Client updatedClient) {
        if (clientRepository.existsByIdAndCpf(id, cpf)) {
            updatedClient.setId(id);
            return clientRepository.save(updatedClient);
        } else {
            throw new RuntimeException("Client not found with id and cpf");
        }
    }

    public boolean deleteByIdAndCpf(Long id, String cpf) {
        if (clientRepository.existsByIdAndCpf(id, cpf)) {
            clientRepository.deleteByIdAndCpf(id, cpf);
            return true;
        }
        return false;
    }
}
