package com.pedro.petshop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.pedro.petshop.dtos.AppointmentDTO;
import com.pedro.petshop.entities.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "pet.id", target = "petId")
    AppointmentDTO toDto(Appointment appointment);

    @Mapping(source = "petId", target = "pet.id")
    Appointment toEntity(AppointmentDTO appointmentDTO);

    default Page<AppointmentDTO> pageToPageDTO(Page<Appointment> appointmentPage) {
        return appointmentPage.map(this::toDto);
    }
}
