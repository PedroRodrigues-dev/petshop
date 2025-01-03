package com.pedro.petshop.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedro.petshop.entities.Appointment;
import com.pedro.petshop.services.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

        private final AppointmentService appointmentService;

        public AppointmentController(AppointmentService appointmentService) {
                this.appointmentService = appointmentService;
        }

        @Operation(summary = "Create a new appointment", description = "Creates a new appointment record in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @PostMapping
        public Appointment createAppointment(@RequestBody Appointment appointment) {
                return appointmentService.create(appointment);
        }

        @Operation(summary = "Get appointment by ID", description = "Retrieves a specific appointment by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Appointment found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Appointment not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Appointment> getAppointmentById(
                        @Parameter(description = "ID of the appointment to be retrieved") @PathVariable Long id) {
                Optional<Appointment> appointment = appointmentService.findById(id);

                return appointment.map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @Operation(summary = "Get all appointments", description = "Retrieves all appointment records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of appointments returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @GetMapping
        public Page<Appointment> getAllAppointments(@Parameter(hidden = true) Pageable pageable) {
                return appointmentService.findAll(pageable);
        }

        @Operation(summary = "Update an existing appointment", description = "Updates an existing appointment record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Appointment not found")
        })
        @PutMapping("/{id}")
        public Appointment updateAppointment(
                        @Parameter(description = "ID of the appointment to be updated") @PathVariable Long id,
                        @RequestBody Appointment appointment) {
                return appointmentService.update(id, appointment);
        }

        @Operation(summary = "Delete an appointment", description = "Deletes an appointment record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Appointment deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Appointment not found")
        })
        @DeleteMapping("/{id}")
        public boolean deleteAppointment(
                        @Parameter(description = "ID of the appointment to be deleted") @PathVariable Long id) {
                return appointmentService.delete(id);
        }
}
