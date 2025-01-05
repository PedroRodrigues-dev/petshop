package com.pedro.petshop.configs;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthentication extends UsernamePasswordAuthenticationToken {

    private final String cpf;
    private final String role;

    public CustomAuthentication(String username, String cpf, String role,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, null, authorities);
        this.cpf = cpf;
        this.role = role;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRole() {
        return role;
    }
}
