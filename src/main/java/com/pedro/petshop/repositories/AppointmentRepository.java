package com.pedro.petshop.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a " +
            "JOIN a.pet p " +
            "JOIN p.client c " +
            "JOIN c.user u " +
            "WHERE a.id = :id AND u.cpf = :cpf")
    Optional<Appointment> findByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

    @Query("SELECT a FROM Appointment a " +
            "JOIN a.pet p " +
            "JOIN p.client c " +
            "JOIN c.user u " +
            "WHERE u.cpf = :cpf")
    Page<Appointment> findAllByUserCpf(@Param("cpf") String cpf, Pageable pageable);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "JOIN a.pet p " +
            "JOIN p.client c " +
            "JOIN c.user u " +
            "WHERE a.id = :id AND u.cpf = :cpf")
    boolean existsByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);

    @Query("DELETE FROM Appointment a " +
            "WHERE a.id = :id AND EXISTS (" +
            "    SELECT 1 FROM Pet p " +
            "    JOIN p.client c " +
            "    JOIN c.user u " +
            "    WHERE p = a.pet AND u.cpf = :cpf)")
    void deleteByIdAndUserCpf(@Param("id") Long id, @Param("cpf") String cpf);
}
