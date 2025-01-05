package com.pedro.petshop.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
        @Query("SELECT ct FROM Contact ct " +
                        "JOIN ct.client c " +
                        "JOIN c.user u " +
                        "WHERE ct.id = :id AND u.cpf = :cpf")
        Optional<Contact> findByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

        @Query("SELECT ct FROM Contact ct " +
                        "JOIN ct.client c " +
                        "JOIN c.user u " +
                        "WHERE u.cpf = :cpf")
        Page<Contact> findAllByUserCpf(@Param("cpf") String cpf, Pageable pageable);

        @Query("SELECT COUNT(ct) > 0 FROM Contact ct " +
                        "JOIN ct.client c " +
                        "JOIN c.user u " +
                        "WHERE c.id = :id AND u.cpf = :cpf")
        boolean existsByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

        @Query("DELETE FROM Contact ct " +
                        "WHERE ct.id = :id AND EXISTS (" +
                        "    SELECT 1 FROM Client c " +
                        "    JOIN c.user u " +
                        "    WHERE c = ct.client AND u.cpf = :cpf)")
        void deleteByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

        @Query("SELECT ct FROM Contact ct " +
                        "JOIN ct.client c " +
                        "WHERE c.id = :clientId")
        Page<Contact> findAllByClientId(@Param("clientId") Long clientId, Pageable pageable);

        @Query("SELECT ct FROM Contact ct " +
                        "JOIN ct.client c " +
                        "JOIN c.user u " +
                        "WHERE c.id = :clientId AND u.cpf = :cpf")
        Page<Contact> findAllByClientIdAndUserCpf(@Param("clientId") Long clientId,
                        @Param("cpf") String cpf,
                        Pageable pageable);
}
