package com.pedro.petshop.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
        @Query("SELECT p FROM Pet p " +
                        "JOIN p.client c " +
                        "JOIN c.user u " +
                        "WHERE p.id = :id AND u.cpf = :cpf")
        Optional<Pet> findByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

        @Query("SELECT p FROM Pet p " +
                        "JOIN p.client c " +
                        "JOIN c.user u " +
                        "WHERE u.cpf = :cpf")
        Page<Pet> findAllByUserCpf(@Param("cpf") String cpf, Pageable pageable);

        @Query("SELECT COUNT(p) > 0 FROM Pet p " +
                        "JOIN p.client c " +
                        "JOIN c.user u " +
                        "WHERE p.id = :id AND u.cpf = :cpf")
        boolean existsByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

        @Query("DELETE FROM Pet p " +
                        "WHERE p.id = :id AND EXISTS (" +
                        "    SELECT 1 FROM Client c " +
                        "    JOIN c.user u " +
                        "    WHERE c = p.client AND u.cpf = :cpf)")
        void deleteByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

        @Query("SELECT p FROM Pet p " +
                        "JOIN p.client c " +
                        "WHERE c.id = :clientId")
        Page<Pet> findAllByClientId(@Param("clientId") Long clientId, Pageable pageable);

        @Query("SELECT p FROM Pet p " +
                        "JOIN p.client c " +
                        "JOIN c.user u " +
                        "WHERE c.id = :clientId AND u.cpf = :cpf")
        Page<Pet> findAllByClientIdAndUserCpf(@Param("clientId") Long clientId,
                        @Param("cpf") String cpf,
                        Pageable pageable);
}
