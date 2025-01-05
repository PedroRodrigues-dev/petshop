package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pedro.petshop.entities.Contact;
import com.pedro.petshop.repositories.ContactRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact create(Contact contact) {
        return contactRepository.save(contact);
    }

    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }

    public Page<Contact> findAll(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }

    public Contact update(Long id, Contact contact) {
        if (contactRepository.existsById(id)) {
            return contactRepository.save(contact);
        }
        return null;
    }

    public boolean delete(Long id) {
        if (contactRepository.existsById(id)) {
            contactRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByIdAndUserCpf(Long id, String cpf) {
        return contactRepository.existsByIdAndUserCpf(id, cpf);
    }

    public Optional<Contact> getByIdAndUserCpf(Long id, String cpf) {
        return contactRepository.findByIdAndUserCpf(id, cpf);
    }

    public Page<Contact> getAllByUserCpf(String cpf, Pageable pageable) {
        return contactRepository.findAllByUserCpf(cpf, pageable);
    }

    public Contact updateByIdAndUserCpf(Long id, String cpf, Contact updatedContact) {
        if (contactRepository.existsByIdAndUserCpf(id, cpf)) {
            updatedContact.setId(id);
            return contactRepository.save(updatedContact);
        } else {
            throw new RuntimeException("Contact not found with id and cpf");
        }
    }

    public boolean deleteByIdAndUserCpf(Long id, String cpf) {
        if (contactRepository.existsByIdAndUserCpf(id, cpf)) {
            contactRepository.deleteByIdAndUserCpf(id, cpf);
            return true;
        }
        return false;
    }
}
