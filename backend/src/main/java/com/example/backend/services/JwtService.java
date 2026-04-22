package com.example.backend.services;

import com.example.backend.dtos.out.profile.ProfileDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    private final long exp = 30 * 60 * 1000;

    public String generateToken(UserDto dto) {
        ProfileDto profile = dto.profile();

        Map<String, String> claims = new HashMap<>();
        claims.put("id", "" + dto.id());
        claims.put("name", dto.name());
        claims.put("email", dto.email());
        claims.put("profileId", "" + (profile != null ? profile.id() : null));
        claims.put("role", (profile != null ? profile.role().name() : null));

        return Jwts.builder()
                .claims(claims)
                .subject(dto.id().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public String generateToken(User user, ProfileDto profileDto) {
        Map<String, String> claims = new HashMap<>();
        claims.put("id", "" + user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("profileId", "" + profileDto.id());
        claims.put("role", profileDto.role().name());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public Map<String, Object> parseClaimsJwsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
