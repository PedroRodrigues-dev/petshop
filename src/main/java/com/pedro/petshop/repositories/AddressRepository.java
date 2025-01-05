package com.pedro.petshop.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a " +
            "JOIN a.client c " +
            "JOIN c.user u " +
            "WHERE a.id = :id AND u.cpf = :cpf")
    Optional<Address> findByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

    @Query("SELECT a FROM Address a " +
            "JOIN a.client c " +
            "JOIN c.user u " +
            "WHERE u.cpf = :cpf")
    Page<Address> findAllByUserCpf(@Param("cpf") String cpf, Pageable pageable);

    @Query("SELECT COUNT(a) > 0 FROM Address a " +
            "JOIN a.client c " +
            "JOIN c.user u " +
            "WHERE a.id = :id AND u.cpf = :cpf")
    boolean existsByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

    @Query("DELETE FROM Address a " +
            "WHERE a.id = :id AND EXISTS (" +
            "    SELECT 1 FROM Client c " +
            "    JOIN c.user u " +
            "    WHERE c = a.client AND u.cpf = :cpf)")
    void deleteByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);
}
