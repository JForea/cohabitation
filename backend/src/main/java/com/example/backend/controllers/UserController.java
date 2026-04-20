package com.example.backend.controllers;

import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.JwtService;
import com.example.backend.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/auth/registry")
    public ResponseEntity<UserDto> create(
            @RequestBody RegisterDto dto,
            HttpServletResponse servletResponse
    ) {
        UserDto userDto = userService.create(dto);
        String token = jwtService.generateToken(userDto);
        ResponseCookie cookie = jwtService.generateCookie(token);
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(
            @RequestBody AuthenticationDto dto,
            HttpServletResponse servletResponse
    ) {
        UserDto userDto = userService.authenticate(dto);
        String token = jwtService.generateToken(userDto);
        ResponseCookie cookie = jwtService.generateCookie(token);
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal CustomUserDetails details) {
        User user = details.getUser();
        return ResponseEntity.ok(new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCurrentProfile()
        ));
    }
}
