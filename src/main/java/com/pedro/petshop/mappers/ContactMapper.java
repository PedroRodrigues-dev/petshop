package com.pedro.petshop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.pedro.petshop.dtos.ContactDTO;
import com.pedro.petshop.entities.Contact;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "client.id", target = "clientId")
    ContactDTO toDto(Contact contact);

    @Mapping(source = "clientId", target = "client.id")
    Contact toEntity(ContactDTO contactDTO);

    default Page<ContactDTO> pageToPageDTO(Page<Contact> contactPage) {
        return contactPage.map(this::toDto);
    }
}
