package com.pedro.petshop.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Breed;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {

    @Query("SELECT b FROM Breed b " +
            "JOIN b.pet p " +
            "JOIN p.client c " +
            "JOIN c.user u " +
            "WHERE b.id = :id AND u.cpf = :cpf")
    Optional<Breed> findByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

    @Query("SELECT b FROM Breed b " +
            "JOIN b.pet p " +
            "JOIN p.client c " +
            "JOIN c.user u " +
            "WHERE u.cpf = :cpf")
    Page<Breed> findAllByUserCpf(@Param("cpf") String cpf, Pageable pageable);

    @Query("SELECT COUNT(b) > 0 FROM Breed b " +
            "JOIN b.pet p " +
            "JOIN p.client c " +
            "JOIN c.user u " +
            "WHERE b.id = :id AND u.cpf = :cpf")
    boolean existsByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

    @Query("DELETE FROM Breed b " +
            "WHERE b.id = :id AND EXISTS (" +
            "    SELECT 1 FROM Pet p " +
            "    JOIN p.client c " +
            "    JOIN c.user u " +
            "    WHERE p = b.pet AND u.cpf = :cpf)")
    void deleteByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);
}
