package com.example.backend.controllers;

import com.example.backend.dtos.in.user.AuthenticationDto;
import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.User;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.services.DeviceTokenService;
import com.example.backend.services.JwtService;
import com.example.backend.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;

    private final DeviceTokenService deviceTokenService;

    public UserController(UserService userService,
                          JwtService jwtService,
                          DeviceTokenService deviceTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.deviceTokenService = deviceTokenService;
    }

    @PostMapping("/auth/registry")
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid RegisterDto dto,
            HttpServletResponse servletResponse
    ) {
        UserDto userDto = userService.create(dto);
        deviceTokenService.save(userDto, dto.deviceToken());
        String token = jwtService.generateToken(userDto);
        servletResponse.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserDto> login(
            @RequestBody @Valid AuthenticationDto dto,
            HttpServletResponse servletResponse
    ) {
        UserDto userDto = userService.authenticate(dto);
        deviceTokenService.save(userDto, dto.deviceToken());
        String token = jwtService.generateToken(userDto);
        servletResponse.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal CustomUserDetails details) {
        User user = details.getUser();
        UserDto dto = userService.getCurrentInfo(user);
        return ResponseEntity.ok(dto);
    }
}
