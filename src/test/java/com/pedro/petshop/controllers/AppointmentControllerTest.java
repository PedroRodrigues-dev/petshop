package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pedro.petshop.dtos.AppointmentDTO;
import com.pedro.petshop.entities.Appointment;
import com.pedro.petshop.mappers.AppointmentMapper;
import com.pedro.petshop.services.AppointmentService;

@SpringBootTest
class AppointmentControllerTest {

    @Autowired
    private AppointmentController appointmentController;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void testGetAllAppointmentsPaged() {
        AppointmentDTO appointmentDTO1 = createAppointment(1L, "Checkup", 100.0);
        AppointmentDTO appointmentDTO2 = createAppointment(2L, "Vaccination", 50.0);
        Appointment appointment1 = appointmentMapper.toEntity(appointmentDTO1);
        Appointment appointment2 = appointmentMapper.toEntity(appointmentDTO2);

        Page<Appointment> mockPage = new PageImpl<>(List.of(appointment1, appointment2), PageRequest.of(0, 10),
                2);

        when(appointmentService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<AppointmentDTO> result = appointmentController.getAllAppointments(pageable);
        assertEquals(2, result.getContent().size());
        assertEquals("Checkup", result.getContent().get(0).getDescription());
        assertEquals("Vaccination", result.getContent().get(1).getDescription());

        assertEquals(10, result.getSize());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testGetAppointmentById_AppointmentExists() {
        AppointmentDTO mockAppointmentDTO = createAppointment(1L, "Checkup", 100.0);
        Appointment mockAppointment = appointmentMapper.toEntity(mockAppointmentDTO);
        when(appointmentService.findById(1L)).thenReturn(Optional.of(mockAppointment));

        ResponseEntity<AppointmentDTO> response = appointmentController.getAppointmentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AppointmentDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Checkup", body.getDescription());
    }

    @Test
    void testGetAppointmentById_AppointmentNotFound() {
        when(appointmentService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<AppointmentDTO> response = appointmentController.getAppointmentById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateAppointment() {
        AppointmentDTO mockAppointmentDTO = createAppointment(null, "Checkup", 100.0);
        Appointment mockAppointment = appointmentMapper.toEntity(mockAppointmentDTO);
        when(appointmentService.create(any(Appointment.class))).thenReturn(mockAppointment);

        AppointmentDTO appointmentToCreate = createAppointment(null, "Checkup", 100.0);

        AppointmentDTO result = appointmentController.createAppointment(appointmentToCreate);

        assertEquals("Checkup", result.getDescription());
        assertEquals(100.0, result.getCost());
    }

    @Test
    void testUpdateAppointment() {
        AppointmentDTO mockAppointmentDTO = createAppointment(1L, "Updated Checkup", 120.0);
        Appointment mockAppointment = appointmentMapper.toEntity(mockAppointmentDTO);
        when(appointmentService.update(any(Long.class), any(Appointment.class))).thenReturn(mockAppointment);

        AppointmentDTO appointmentToUpdate = createAppointment(1L, "Updated Checkup", 120.0);

        AppointmentDTO result = appointmentController.updateAppointment(1L, appointmentToUpdate);

        assertEquals("Updated Checkup", result.getDescription());
        assertEquals(120.0, result.getCost());
    }

    @Test
    void testDeleteAppointment() {
        when(appointmentService.delete(1L)).thenReturn(true);

        boolean result = appointmentController.deleteAppointment(1L);

        assertEquals(true, result);
    }

    private AppointmentDTO createAppointment(Long id, String description, Double cost) {
        AppointmentDTO appointment = new AppointmentDTO();
        appointment.setId(id);
        appointment.setDescription(description);
        appointment.setCost(cost);
        appointment.setDate(null);
        appointment.setPetId(null);
        return appointment;
    }
}
