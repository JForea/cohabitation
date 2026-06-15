package com.example.backend.factories;

import com.example.backend.annotations.WithMockCustomUser;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @NotNull
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User mockedUser = Mockito.mock(User.class);

        CustomUserDetails principal = new CustomUserDetails(mockedUser);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + annotation.role()));
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);

        context.setAuthentication(auth);
        return context;
    }

}
