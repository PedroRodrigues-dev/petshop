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

import com.pedro.petshop.entities.Address;
import com.pedro.petshop.services.AddressService;

@SpringBootTest
class AddressControllerTest {

    @Autowired
    private AddressController addressController;

    @MockitoBean
    private AddressService addressService;

    @Test
    void testCreateAddress() {
        Address address = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 101", "Home");
        when(addressService.create(any(Address.class))).thenReturn(address);

        Address result = addressController.createAddress(address);

        assertEquals("Street 123", result.getStreet());
        assertEquals("City1", result.getCity());
        assertEquals("Neighborhood1", result.getNeighborhood());
        assertEquals("Apt 101", result.getComplement());
        assertEquals("Home", result.getTag());
    }

    @Test
    void testGetAddressById_AddressExists() {
        Address address = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 101", "Home");
        when(addressService.findById(1L)).thenReturn(Optional.of(address));

        ResponseEntity<Address> response = addressController.getAddressById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Address body = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertEquals("Street 123", body.getStreet());
    }

    @Test
    void testGetAddressById_AddressNotFound() {
        when(addressService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Address> response = addressController.getAddressById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllAddresses() {
        Address address1 = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 101", "Home");
        Address address2 = createAddress(2L, "Street 456", "City2", "Neighborhood2", "Apt 202", "Work");
        Page<Address> mockPage = new PageImpl<>(List.of(address1, address2), PageRequest.of(0, 10), 2);

        when(addressService.findAll(any())).thenReturn(mockPage);

        Page<Address> result = addressController.getAllAddresses(PageRequest.of(0, 10));

        assertEquals(2, result.getContent().size());
        assertEquals("Street 123", result.getContent().get(0).getStreet());
        assertEquals("Street 456", result.getContent().get(1).getStreet());
    }

    @Test
    void testUpdateAddress() {
        Address updatedAddress = createAddress(1L, "Street 123", "City1", "Neighborhood1", "Apt 102", "Work");
        when(addressService.update(1L, updatedAddress)).thenReturn(updatedAddress);

        Address result = addressController.updateAddress(1L, updatedAddress);

        assertEquals("Apt 102", result.getComplement());
        assertEquals("Work", result.getTag());
    }

    @Test
    void testDeleteAddress() {
        when(addressService.delete(1L)).thenReturn(true);

        boolean result = addressController.deleteAddress(1L);

        assertEquals(true, result);
    }

    private Address createAddress(Long id, String street, String city, String neighborhood, String complement,
            String tag) {
        Address address = new Address();
        address.setId(id);
        address.setStreet(street);
        address.setCity(city);
        address.setNeighborhood(neighborhood);
        address.setComplement(complement);
        address.setTag(tag);
        return address;
    }
}
