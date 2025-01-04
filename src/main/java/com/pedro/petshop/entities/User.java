package com.pedro.petshop.entities;

import com.pedro.petshop.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    private String cpf;

    private String name;
    private Role role;
    private String password;
}
