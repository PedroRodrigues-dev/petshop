package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pedro.petshop.configs.Tool;
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

    public Page<Address> findAllByClientId(Long clientId, Pageable pageable) {
        return addressRepository.findAllByClientId(clientId, pageable);
    }

    public Page<Address> findAllByClientIdAndUserCpf(Long clientId, String cpf,
            Pageable pageable) {
        return addressRepository.findAllByClientIdAndUserCpf(clientId, cpf,
                pageable);
    }

    public Address update(Long id, Address address) {
        address.setId(id);
        return addressRepository.findById(id).map(existingAddress -> {
            BeanUtils.copyProperties(address, existingAddress, Tool.getNullPropertyNames(address));
            return addressRepository.save(existingAddress);
        }).orElse(null);
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
        updatedAddress.setId(id);
        return addressRepository.findByIdAndUserCpf(id, cpf).map(existingAddress -> {
            BeanUtils.copyProperties(updatedAddress, existingAddress, Tool.getNullPropertyNames(updatedAddress));
            return addressRepository.save(existingAddress);
        }).orElse(null);
    }

    public boolean deleteByIdAndUserCpf(Long id, String cpf) {
        if (addressRepository.existsByIdAndUserCpf(id, cpf)) {
            addressRepository.deleteByIdAndUserCpf(id, cpf);
            return true;
        }
        return false;
    }
}
