package com.pedro.petshop.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "clients")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;
    private String name;
    private String cpf;
    private LocalDateTime registrationDate;

    @ManyToOne
    @JoinColumn(name = "cpf", referencedColumnName = "cpf", insertable = false, updatable = false)
    private User user;
}
