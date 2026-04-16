package com.example.backend.services;

import com.example.backend.dtos.in.user.RegisterDto;
import com.example.backend.dtos.out.user.AuthDto;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Color;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private final Random random;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder,
                       ObjectMapper objectMapper) {
        this.random = new Random();
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    private Color getRandomColor() {
        Color[] colors = Color.values();
        return colors[random.nextInt(colors.length)];
    }

    public AuthDto create(RegisterDto dto) {
        User user = repository.save(new User(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.name(),
                dto.male(),
                getRandomColor()
        ));

        return new AuthDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCurrentProfile()
        );
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getOne(Integer id) {
        Optional<User> userOptional = repository.findById(id);
        return userOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    public List<User> getMany(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    public User patch(Integer id, JsonNode patchNode) throws IOException {
        User user = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(user).readValue(patchNode);

        return repository.save(user);
    }

    public List<Integer> patchMany(List<Integer> ids, JsonNode patchNode) throws IOException {
        Collection<User> users = repository.findAllById(ids);

        for (User user : users) {
            objectMapper.readerForUpdating(user).readValue(patchNode);
        }

        List<User> resultUsers = repository.saveAll(users);
        return resultUsers.stream()
                .map(User::getId)
                .toList();
    }

    public User delete(Integer id) {
        User user = repository.findById(id).orElse(null);
        if (user != null) {
            repository.delete(user);
        }
        return user;
    }

    public void deleteMany(List<Integer> ids) {
        repository.deleteAllById(ids);
    }
}
