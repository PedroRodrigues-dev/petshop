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
}
