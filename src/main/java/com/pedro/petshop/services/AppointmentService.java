package com.pedro.petshop.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pedro.petshop.configs.Tool;
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

    public Page<Appointment> findAllByClientId(Long clientId, Pageable pageable) {
        return appointmentRepository.findAllByClientId(clientId, pageable);
    }

    public Page<Appointment> findAllByClientIdAndUserCpf(Long clientId, String cpf, Pageable pageable) {
        return appointmentRepository.findAllByClientIdAndUserCpf(clientId, cpf, pageable);
    }

    public Page<Appointment> findAllByPetId(Long petId, Pageable pageable) {
        return appointmentRepository.findAllByPetId(petId, pageable);
    }

    public Page<Appointment> findAllByPetIdAndUserCpf(Long petId, String cpf, Pageable pageable) {
        return appointmentRepository.findAllByPetIdAndUserCpf(petId, cpf, pageable);
    }

    public Appointment update(Long id, Appointment appointment) {
        appointment.setId(id);
        return appointmentRepository.findById(id).map(existingAppointment -> {
            BeanUtils.copyProperties(appointment, existingAppointment, Tool.getNullPropertyNames(appointment));
            return appointmentRepository.save(existingAppointment);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByIdAndUserCpf(Long id, String cpf) {
        return appointmentRepository.existsByIdAndUserCpf(id, cpf);
    }

    public Optional<Appointment> getByIdAndUserCpf(Long id, String cpf) {
        return appointmentRepository.findByIdAndUserCpf(id, cpf);
    }

    public Page<Appointment> getAllByUserCpf(String cpf, Pageable pageable) {
        return appointmentRepository.findAllByUserCpf(cpf, pageable);
    }

    public Appointment updateByIdAndUserCpf(Long id, String cpf, Appointment updatedAppointment) {
        updatedAppointment.setId(id);
        return appointmentRepository.findByIdAndUserCpf(id, cpf).map(existingAppointment -> {
            BeanUtils.copyProperties(updatedAppointment, existingAppointment, Tool.getNullPropertyNames(
                    updatedAppointment));
            return appointmentRepository.save(existingAppointment);
        }).orElse(null);
    }

    public boolean deleteByIdAndUserCpf(Long id, String cpf) {
        if (appointmentRepository.existsByIdAndUserCpf(id, cpf)) {
            appointmentRepository.deleteByIdAndUserCpf(id, cpf);
            return true;
        }
        return false;
    }
}
