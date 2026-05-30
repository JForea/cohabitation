package com.example.backend.services;

import com.example.backend.dtos.in.tokens.DeviceTokenDto;
import com.example.backend.dtos.out.user.UserDto;
import com.example.backend.entities.DeviceToken;
import com.example.backend.entities.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repositories.DeviceTokenRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.types.Platform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceTokenServiceTest {

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeviceTokenService deviceTokenService;

    @Test
    void shouldCreateNewOnSaveIfNotExists() {
        Integer userId = 1;

        String deviceId = "deviceId";

        UserDto userDto = mock(UserDto.class);
        when(userDto.id()).thenReturn(userId);

        DeviceTokenDto dto = new DeviceTokenDto(
                deviceId,
                "fcmToken",
                Platform.ANDROID
        );

        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(deviceTokenRepository.findByUserAndDeviceId(user, deviceId))
                .thenReturn(Optional.empty());

        when(deviceTokenRepository.save(any(DeviceToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        deviceTokenService.save(userDto, dto);

        verify(userRepository).findById(userId);
        verify(deviceTokenRepository).findByUserAndDeviceId(user, deviceId);
        verify(deviceTokenRepository).save(any(DeviceToken.class));
    }

    @Test
    void shouldUpdateOnSaveIfExists() {
        Integer userId = 1;

        String deviceId1 = "deviceId1";
        String newFcmToken = "newFcmToken";

        UserDto userDto = mock(UserDto.class);
        when(userDto.id()).thenReturn(userId);

        DeviceTokenDto dto = new DeviceTokenDto(
                deviceId1,
                newFcmToken,
                Platform.ANDROID
        );

        User user = new User();

        DeviceToken existingToken = new DeviceToken(
                user,
                deviceId1,
                "oldFcmToken",
                Platform.IOS
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(deviceTokenRepository.findByUserAndDeviceId(user, deviceId1))
                .thenReturn(Optional.of(existingToken));

        when(deviceTokenRepository.save(any(DeviceToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        deviceTokenService.save(userDto, dto);

        verify(deviceTokenRepository).save(existingToken);

        assertEquals(newFcmToken, existingToken.getToken());
        assertEquals(Platform.ANDROID, existingToken.getPlatform());
    }

    @Test
    void shouldDoNothingOnSaveIfDtoNull() {
        UserDto userDto = mock(UserDto.class);

        deviceTokenService.save(userDto, null);

        verifyNoInteractions(userRepository);
        verifyNoInteractions(deviceTokenRepository);
    }

    @Test
    void shouldThrowOnSaveIfUserNotFound() {
        Integer userId = 1;

        UserDto userDto = mock(UserDto.class);
        when(userDto.id()).thenReturn(userId);

        DeviceTokenDto dto = new DeviceTokenDto(
                "deviceId",
                "fcmToken",
                Platform.ANDROID
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> deviceTokenService.save(userDto, dto));

        verify(userRepository).findById(userId);
        verifyNoInteractions(deviceTokenRepository);
    }

    @Test
    void shouldDelete() {
        Integer userId = 1;

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        String deviceId = "deviceId";

        deviceTokenService.delete(user, deviceId);

        verify(deviceTokenRepository).deleteByUser_IdAndDeviceId(userId, deviceId);
    }

}
