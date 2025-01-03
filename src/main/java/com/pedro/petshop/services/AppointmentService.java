package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pedro.petshop.entities.Appointment;
import com.pedro.petshop.repositories.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment create(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Page<Appointment> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    public Appointment update(Long id, Appointment appointment) {
        if (appointmentRepository.existsById(id)) {
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    public boolean delete(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
