package com.pedro.petshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
