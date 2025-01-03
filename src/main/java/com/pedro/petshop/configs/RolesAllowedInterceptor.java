package com.pedro.petshop.configs;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.pedro.petshop.entities.User;
import com.pedro.petshop.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RolesAllowedInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            RolesAllowed rolesAllowed = method.getMethodAnnotation(RolesAllowed.class);

            if (rolesAllowed != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }

                String name = authentication.getName();
                User user = userRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco de dados"));

                List<String> allowedRoles = Arrays.asList(rolesAllowed.value());
                if (!allowedRoles.contains(user.getRole().name())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }

        return true;
    }
}
