package com.example.backend.security;

import com.example.backend.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ApartmentSecurity {
    public boolean hasAccess(Integer apartmentId, Authentication auth) {
        User user = (User) auth.getPrincipal();

        System.out.println(user == null ? "user is null" : user.toString());

        if (user == null)
            return false;
        return Objects.equals(user.getCurrentProfile().getApartment().getId(), apartmentId);
    }
}
