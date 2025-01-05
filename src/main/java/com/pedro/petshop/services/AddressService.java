package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pedro.petshop.entities.Address;
import com.pedro.petshop.repositories.AddressRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address create(Address address) {
        return addressRepository.save(address);
    }

    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    public Page<Address> findAll(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }

    public Address update(Long id, Address address) {
        if (addressRepository.existsById(id)) {
            return addressRepository.save(address);
        }
        return null;
    }

    public boolean delete(Long id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByIdAndUserCpf(Long id, String cpf) {
        return addressRepository.existsByIdAndUserCpf(id, cpf);
    }

    public Optional<Address> getByIdAndUserCpf(Long id, String cpf) {
        return addressRepository.findByIdAndUserCpf(id, cpf);
    }

    public Page<Address> getAllByUserCpf(String cpf, Pageable pageable) {
        return addressRepository.findAllByUserCpf(cpf, pageable);
    }

    public Address updateByIdAndUserCpf(Long id, String cpf, Address updatedAddress) {
        if (addressRepository.existsByIdAndUserCpf(id, cpf)) {
            updatedAddress.setId(id);
            return addressRepository.save(updatedAddress);
        } else {
            throw new RuntimeException("Address not found with id and cpf");
        }
    }

    public boolean deleteByIdAndUserCpf(Long id, String cpf) {
        if (addressRepository.existsByIdAndUserCpf(id, cpf)) {
            addressRepository.deleteByIdAndUserCpf(id, cpf);
            return true;
        }
        return false;
    }
}
