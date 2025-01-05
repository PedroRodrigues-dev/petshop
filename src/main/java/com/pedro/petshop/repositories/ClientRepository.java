package com.pedro.petshop.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByIdAndCpf(Long id, String cpf);

    Page<Client> findAllByCpf(String cpf, Pageable pageable);

    boolean existsByIdAndCpf(Long id, String cpf);

    void deleteByIdAndCpf(Long id, String cpf);
}
