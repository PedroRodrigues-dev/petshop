package com.pedro.petshop.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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

import com.pedro.petshop.entities.Appointment;
import com.pedro.petshop.services.AppointmentService;

@SpringBootTest
class AppointmentControllerTest {

    @Autowired
    private AppointmentController appointmentController;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void testGetAllAppointmentsPaged() {
        List<Appointment> mockAppointments = Arrays.asList(
                createAppointment(1L, "Checkup", 100.0),
                createAppointment(2L, "Vaccination", 50.0));

        Page<Appointment> mockPage = new PageImpl<>(mockAppointments, PageRequest.of(0, 10), mockAppointments.size());

        when(appointmentService.findAll(any(Pageable.class))).thenReturn(mockPage);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Appointment> result = appointmentController.getAllAppointments(pageable);
        assertEquals(2, result.getContent().size());
        assertEquals("Checkup", result.getContent().get(0).getDescription());
        assertEquals("Vaccination", result.getContent().get(1).getDescription());

        assertEquals(10, result.getSize());
        assertEquals(0, result.getNumber());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testGetAppointmentById_AppointmentExists() {
        Appointment mockAppointment = createAppointment(1L, "Checkup", 100.0);
        when(appointmentService.findById(1L)).thenReturn(Optional.of(mockAppointment));

        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Appointment body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Checkup", body.getDescription());
    }

    @Test
    void testGetAppointmentById_AppointmentNotFound() {
        when(appointmentService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Appointment> response = appointmentController.getAppointmentById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateAppointment() {
        Appointment mockAppointment = createAppointment(null, "Checkup", 100.0);
        when(appointmentService.create(any(Appointment.class))).thenReturn(mockAppointment);

        Appointment appointmentToCreate = createAppointment(null, "Checkup", 100.0);

        Appointment result = appointmentController.createAppointment(appointmentToCreate);

        assertEquals("Checkup", result.getDescription());
        assertEquals(100.0, result.getCost());
    }

    @Test
    void testUpdateAppointment() {
        Appointment mockAppointment = createAppointment(1L, "Updated Checkup", 120.0);
        when(appointmentService.update(any(Long.class), any(Appointment.class))).thenReturn(mockAppointment);

        Appointment appointmentToUpdate = createAppointment(1L, "Updated Checkup", 120.0);

        Appointment result = appointmentController.updateAppointment(1L, appointmentToUpdate);

        assertEquals("Updated Checkup", result.getDescription());
        assertEquals(120.0, result.getCost());
    }

    @Test
    void testDeleteAppointment() {
        when(appointmentService.delete(1L)).thenReturn(true);

        boolean result = appointmentController.deleteAppointment(1L);

        assertEquals(true, result);
    }

    private Appointment createAppointment(Long id, String description, Double cost) {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setDescription(description);
        appointment.setCost(cost);
        appointment.setDate(null); // set the date as null for now
        appointment.setPet(null); // set the pet as null for now
        return appointment;
    }
}
