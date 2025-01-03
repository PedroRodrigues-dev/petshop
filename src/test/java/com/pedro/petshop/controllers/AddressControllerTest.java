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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.pedro.petshop.dtos.AddressDTO;
import com.pedro.petshop.entities.Address;
import com.pedro.petshop.mappers.AddressMapper;
import com.pedro.petshop.services.AddressService;

@SpringBootTest
class AddressControllerTest {

    @Autowired
    private AddressController addressController;

    @Autowired
    private AddressMapper addressMapper;

    @MockitoBean
    private AddressService addressService;

    @Test
    void testCreateAddress() {
        AddressDTO addressDTO = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 101", "Home");
        Address address = addressMapper.toEntity(addressDTO);
        when(addressService.create(address)).thenReturn(address);

        AddressDTO result = addressController.createAddress(addressDTO);

        assertEquals("Street 123", result.getStreet());
        assertEquals("City1", result.getCity());
        assertEquals("Neighborhood1", result.getNeighborhood());
        assertEquals("Apt 101", result.getComplement());
        assertEquals("Home", result.getTag());
    }

    @Test
    void testGetAddressById_AddressExists() {
        AddressDTO addressDTO = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 101", "Home");
        Address address = addressMapper.toEntity(addressDTO);
        when(addressService.findById(1L)).thenReturn(Optional.of(address));

        ResponseEntity<AddressDTO> response = addressController.getAddressById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AddressDTO body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Street 123", body.getStreet());
    }

    @Test
    void testGetAddressById_AddressNotFound() {
        when(addressService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<AddressDTO> response = addressController.getAddressById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllAddresses() {
        AddressDTO addressDTO1 = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 101", "Home");
        AddressDTO addressDTO2 = createAddress(2L, "Street 456", "City2", "Neighborhood2", "Apt 202", "Work");
        Address address1 = addressMapper.toEntity(addressDTO1);
        Address address2 = addressMapper.toEntity(addressDTO2);
        Page<Address> mockPage = new PageImpl<>(List.of(address1, address2), PageRequest.of(0, 10), 2);

        when(addressService.findAll(any())).thenReturn(mockPage);

        Page<AddressDTO> result = addressController.getAllAddresses(PageRequest.of(0, 10));

        assertEquals(2, result.getContent().size());
        assertEquals("Street 123", result.getContent().get(0).getStreet());
        assertEquals("Street 456", result.getContent().get(1).getStreet());
    }

    @Test
    void testUpdateAddress() {
        AddressDTO updatedAddressDTO = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 102", "Work");
        Address updatedAddress = addressMapper.toEntity(updatedAddressDTO);

        when(addressService.update(1L, updatedAddress)).thenReturn(updatedAddress);

        AddressDTO result = addressController.updateAddress(1L, updatedAddressDTO);

        assertEquals("Apt 102", result.getComplement());
        assertEquals("Work", result.getTag());
    }

    @Test
    void testDeleteAddress() {
        when(addressService.delete(1L)).thenReturn(true);

        boolean result = addressController.deleteAddress(1L);

        assertEquals(true, result);
    }

    private AddressDTO createAddress(Long id, String street, String city, String neighborhood, String complement,
            String tag) {
        AddressDTO address = new AddressDTO();
        address.setId(id);
        address.setStreet(street);
        address.setCity(city);
        address.setNeighborhood(neighborhood);
        address.setComplement(complement);
        address.setTag(tag);
        return address;
    }
}
