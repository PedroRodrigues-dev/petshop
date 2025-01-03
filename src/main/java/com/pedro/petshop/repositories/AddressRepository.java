package com.pedro.petshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedro.petshop.entities.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
