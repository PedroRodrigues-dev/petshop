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

import com.pedro.petshop.configs.RolesAllowed;
import com.pedro.petshop.dtos.AppointmentDTO;
import com.pedro.petshop.entities.Appointment;
import com.pedro.petshop.mappers.AppointmentMapper;
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
        private final AppointmentMapper appointmentMapper;

        public AppointmentController(AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
                this.appointmentService = appointmentService;
                this.appointmentMapper = appointmentMapper;
        }

        @Operation(summary = "Create a new appointment", description = "Creates a new appointment record in the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN" })
        @PostMapping
        public AppointmentDTO createAppointment(@RequestBody AppointmentDTO appointment) {
                return appointmentMapper.toDto(appointmentService.create(appointmentMapper.toEntity(appointment)));
        }

        @Operation(summary = "Get appointment by ID", description = "Retrieves a specific appointment by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Appointment found"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Appointment not found")
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping("/{id}")
        public ResponseEntity<AppointmentDTO> getAppointmentById(
                        @Parameter(description = "ID of the appointment to be retrieved") @PathVariable("id") Long id) {
                Optional<Appointment> appointment = appointmentService.findById(id);

                if (appointment.isPresent())
                        return ResponseEntity.ok(appointmentMapper.toDto(appointment.get()));

                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Get all appointments", description = "Retrieves all appointment records", parameters = {
                        @Parameter(name = "page", description = "Page number (0-based index)", in = ParameterIn.QUERY, example = "0"),
                        @Parameter(name = "size", description = "Number of items per page", in = ParameterIn.QUERY, example = "10") })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of appointments returned"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
        })
        @RolesAllowed({ "ADMIN" })
        @GetMapping
        public Page<AppointmentDTO> getAllAppointments(@Parameter(hidden = true) Pageable pageable) {
                return appointmentMapper.pageToPageDTO(appointmentService.findAll(pageable));
        }

        @Operation(summary = "Update an existing appointment", description = "Updates an existing appointment record")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Appointment not found")
        })
        @RolesAllowed({ "ADMIN" })
        @PutMapping("/{id}")
        public AppointmentDTO updateAppointment(
                        @Parameter(description = "ID of the appointment to be updated") @PathVariable("id") Long id,
                        @RequestBody AppointmentDTO appointment) {
                return appointmentMapper.toDto(appointmentService.update(id, appointmentMapper.toEntity(appointment)));
        }

        @Operation(summary = "Delete an appointment", description = "Deletes an appointment record by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Appointment deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden"),
                        @ApiResponse(responseCode = "404", description = "Appointment not found")
        })
        @RolesAllowed({ "ADMIN" })
        @DeleteMapping("/{id}")
        public boolean deleteAppointment(
                        @Parameter(description = "ID of the appointment to be deleted") @PathVariable("id") Long id) {
                return appointmentService.delete(id);
        }
}
