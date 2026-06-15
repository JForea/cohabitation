package com.example.backend.services;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.User;
import com.example.backend.types.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "secret", "my-secret-key-my-secret-key-my-secret-key-123");
        ReflectionTestUtils.setField(jwtService, "exp", 1000000L);
    }

    @Test
    void shouldGenerateToken() {
        UserDto dto = mock(UserDto.class);
        ProfileDto profile = mock(ProfileDto.class);

        when(dto.id()).thenReturn(1);
        when(dto.name()).thenReturn("Марк");
        when(dto.email()).thenReturn("test@gmail.com");
        when(dto.profile()).thenReturn(profile);

        when(profile.id()).thenReturn(10L);
        when(profile.role()).thenReturn(Role.INHABITANT);

        String token = jwtService.generateToken(dto);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldGenerateTokenWithProfileNull() {
        UserDto dto = mock(UserDto.class);

        when(dto.id()).thenReturn(1);
        when(dto.name()).thenReturn("Марк");
        when(dto.email()).thenReturn("test@gmail.com");
        when(dto.profile()).thenReturn(null);

        String token = jwtService.generateToken(dto);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldGenerateTokenWithUser() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(1);
        when(user.getName()).thenReturn("Марк");
        when(user.getEmail()).thenReturn("test@gmail.com");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldGenerateTokenWithNullFields() {
        User user = mock(User.class);

        when(user.getId()).thenReturn(1);
        when(user.getName()).thenReturn(null);
        when(user.getEmail()).thenReturn(null);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldGenerateTokenWithUserAndProfile() {
        User user = mock(User.class);
        ProfileDto profileDto = mock(ProfileDto.class);

        when(user.getId()).thenReturn(1);
        when(user.getName()).thenReturn("Марк");
        when(user.getEmail()).thenReturn("test@gmail.com");

        when(profileDto.id()).thenReturn(10L);
        when(profileDto.role()).thenReturn(Role.INHABITANT);

        String token = jwtService.generateToken(user, profileDto);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldGenerateTokenWithUserAndProfileNullFields() {
        User user = mock(User.class);
        ProfileDto profileDto = mock(ProfileDto.class);

        when(user.getId()).thenReturn(1);
        when(user.getName()).thenReturn(null);
        when(user.getEmail()).thenReturn(null);

        when(profileDto.id()).thenReturn(10L);
        when(profileDto.role()).thenReturn(Role.INHABITANT);

        String token = jwtService.generateToken(user, profileDto);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldParseClaimsFromToken() {
        User user = mock(User.class);
        ProfileDto profileDto = mock(ProfileDto.class);

        when(user.getId()).thenReturn(1);
        when(user.getName()).thenReturn("Марк");
        when(user.getEmail()).thenReturn("test@gmail.com");

        when(profileDto.id()).thenReturn(10L);
        when(profileDto.role()).thenReturn(Role.INHABITANT);

        String token = jwtService.generateToken(user, profileDto);

        Map<String, Object> claims = jwtService.parseClaimsJwsFromToken(token);

        assertEquals("1", claims.get("id"));
        assertEquals("Марк", claims.get("name"));
        assertEquals("test@gmail.com", claims.get("email"));
        assertEquals("10", claims.get("profileId"));
        assertEquals(Role.INHABITANT.name(), claims.get("role"));
    }

    @Test
    void shouldThrowOnParseClaimsIfTokenInvalid() {
        String invalidToken = "not.valid.jwt";

        assertThrows(Exception.class,
                () -> jwtService.parseClaimsJwsFromToken(invalidToken));
    }

}
