package com.example.backend.security;

import com.example.backend.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ApartmentSecurity {
    private static final Logger log = LoggerFactory.getLogger(ApartmentSecurity.class);

    public boolean hasAccess(Integer apartmentId, Authentication auth) {
        User user = (User) auth.getPrincipal();

        log.info("I'm working");

        if (user == null) {
            log.info("User is null.");
            return false;
        }
        if (Objects.equals(user.getCurrentProfile().getApartment().getId(), apartmentId))
            return true;
        else {
            log.info("Forbidden for: {}", user.toString());
            return false;
        }
    }
}
