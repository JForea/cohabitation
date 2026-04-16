package com.example.backend.security;

import com.example.backend.entities.Profile;
import com.example.backend.entities.User;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Profile profile = user.getCurrentProfile();
        if (profile == null)
            return List.of(new SimpleGrantedAuthority("ROLE_HOUSELESS"));

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + profile.getRole().name())
        );
    }

    @Override
    @NonNull
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
}
