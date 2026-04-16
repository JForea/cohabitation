package com.example.backend.services;

import com.example.backend.dtos.out.user.AuthDto;
import com.example.backend.entities.Profile;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(AuthDto dto) {
        Profile profile = dto.profile();

        Map<String, String> claims = new HashMap<>();
        claims.put("id", "" + dto.id());
        claims.put("name", dto.name());
        claims.put("email", dto.email());
        claims.put("profileId", "" + (profile != null ? profile.getId() : null));
        claims.put("role", (profile != null ? profile.getRole().name() : null));

        long exp = 30 * 60 * 1000;
        return Jwts.builder()
                .claims(claims)
                .subject(dto.id().toString())
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

    public ResponseCookie generateCookie(String token) {
        // TODO: уменьшить время jwt
        return ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();
    }
}
