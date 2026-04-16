package com.example.backend.controllers;

import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.AuthDto;
import com.example.backend.entities.User;
import com.example.backend.services.JwtService;
import com.example.backend.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;

    public UserController(UserService userService,
                          JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<AuthDto> create(
            @RequestBody RegisterDto dto,
            HttpServletResponse servletResponse
    ) {
        AuthDto authDto = userService.create(dto);
        String token = jwtService.generateToken(authDto);
        ResponseCookie cookie = jwtService.generateCookie(token);
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(authDto);
    }

    @PatchMapping("/{id}")
    public User patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
        return userService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
        return userService.patchMany(ids, patchNode);
    }

}
