package com.example.backend.services;

import com.example.backend.dtos.in.tokens.DeviceTokenDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.DeviceToken;
import com.example.backend.entities.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.DeviceTokenRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    private final UserRepository userRepository;

    public DeviceTokenService(DeviceTokenRepository deviceTokenRepository,
                              UserRepository userRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.userRepository = userRepository;
    }

    public void save(UserDto userDto, DeviceTokenDto dto) {
        if (dto == null) return;

        User user = userRepository.findById(userDto.id()).orElseThrow(
                () -> new ResourceNotFoundException("User not found during deviceToken saving.")
        );

        Optional<DeviceToken> existing =
                deviceTokenRepository.findByUserAndDeviceId(user, dto.deviceId());

        if (existing.isPresent()) {
            DeviceToken token = existing.get();
            token.setToken(dto.token());
            token.setPlatform(dto.platform());
            deviceTokenRepository.save(token);
        } else {
            deviceTokenRepository.save(new DeviceToken(
                    user,
                    dto.deviceId(),
                    dto.token(),
                    dto.platform()
            ));
        }
    }

}
