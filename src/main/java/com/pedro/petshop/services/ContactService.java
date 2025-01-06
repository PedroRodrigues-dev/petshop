package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pedro.petshop.configs.Tool;
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

    public Page<Contact> findAllByClientId(Long clientId, Pageable pageable) {
        return contactRepository.findAllByClientId(clientId, pageable);
    }

    public Page<Contact> findAllByClientIdAndUserCpf(Long clientId, String cpf,
            Pageable pageable) {
        return contactRepository.findAllByClientIdAndUserCpf(clientId, cpf,
                pageable);
    }

    public Contact update(Long id, Contact contact) {
        contact.setId(id);
        return contactRepository.findById(id).map(existingContact -> {
            BeanUtils.copyProperties(contact, existingContact, Tool.getNullPropertyNames(contact));
            return contactRepository.save(existingContact);
        }).orElse(null);
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
        updatedContact.setId(id);
        return contactRepository.findByIdAndUserCpf(id, cpf).map(existingContact -> {
            BeanUtils.copyProperties(updatedContact, existingContact, Tool.getNullPropertyNames(updatedContact));
            return contactRepository.save(existingContact);
        }).orElse(null);
    }

    public boolean deleteByIdAndUserCpf(Long id, String cpf) {
        if (contactRepository.existsByIdAndUserCpf(id, cpf)) {
            contactRepository.deleteByIdAndUserCpf(id, cpf);
            return true;
        }
        return false;
    }
}
