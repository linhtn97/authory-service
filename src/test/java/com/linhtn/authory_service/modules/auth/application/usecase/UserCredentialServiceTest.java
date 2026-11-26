package com.linhtn.authory_service.modules.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.linhtn.authory_service.modules.auth.application.dto.result.UserCredentialResult;
import com.linhtn.authory_service.modules.auth.application.mapper.UserCredentialMapper;
import com.linhtn.authory_service.modules.auth.domain.model.UserCredential;
import com.linhtn.authory_service.modules.auth.domain.repository.UserCredentialRepository;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserAccountLockedException;
import com.linhtn.authory_service.modules.auth.exception.unchecked.UserCredentialInvalidException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class UserCredentialServiceTest {

    @Mock private MessageResolver i18nHelper;
    @Mock private UserCredentialRepository userCredentialRepository;
    @Mock private UserCredentialMapper userCredentialMapper;
    @Mock private PasswordEncoder passwordEncoder;

    private UserCredentialService service;

    @BeforeEach
    void setUp() {
        service =
                new UserCredentialService(
                        i18nHelper,
                        userCredentialRepository,
                        userCredentialMapper,
                        passwordEncoder);
    }

    @Test
    @DisplayName("Should successfully validate login and reset failed attempts")
    void shouldValidateAndTrackLoginSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserCredential credential =
                UserCredential.builder()
                        .userId(userId)
                        .password("hashedPassword")
                        .failedLoginAttempts(2)
                        .lockedUntil(null)
                        .build();

        UserCredentialResult dto = UserCredentialResult.builder().userId(userId).build();

        when(userCredentialRepository.findByUserId(userId)).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("rawPassword", "hashedPassword")).thenReturn(true);
        when(userCredentialRepository.update(any(UserCredential.class))).thenReturn(credential);
        when(userCredentialMapper.toDto(any(UserCredential.class))).thenReturn(dto);

        UserCredentialResult result = service.validateAndTrackLogin(userId, "rawPassword");

        assertThat(result).isNotNull();
        assertThat(credential.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(credential.getLockedUntil()).isNull();
        verify(userCredentialRepository).update(credential);
    }

    @Test
    @DisplayName("Should throw UserAccountLockedException when account is already locked")
    void shouldThrowWhenAccountIsAlreadyLocked() {
        UUID userId = UUID.randomUUID();
        UserCredential credential =
                UserCredential.builder()
                        .userId(userId)
                        .password("hashedPassword")
                        .failedLoginAttempts(5)
                        .lockedUntil(LocalDateTime.now().plusMinutes(10))
                        .build();

        when(userCredentialRepository.findByUserId(userId)).thenReturn(Optional.of(credential));
        when(i18nHelper.getMessage(any())).thenReturn("Account is locked");

        assertThatThrownBy(() -> service.validateAndTrackLogin(userId, "rawPassword"))
                .isInstanceOf(UserAccountLockedException.class);
    }

    @Test
    @DisplayName("Should lock account for 15 minutes when 5th consecutive failed attempt occurs")
    void shouldLockAccountOnFifthFailedAttempt() {
        UUID userId = UUID.randomUUID();
        UserCredential credential =
                UserCredential.builder()
                        .userId(userId)
                        .password("hashedPassword")
                        .failedLoginAttempts(4)
                        .lockedUntil(null)
                        .build();

        when(userCredentialRepository.findByUserId(userId)).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);
        when(i18nHelper.getMessage(any())).thenReturn("Account locked");

        assertThatThrownBy(() -> service.validateAndTrackLogin(userId, "wrongPassword"))
                .isInstanceOf(UserAccountLockedException.class);

        assertThat(credential.getFailedLoginAttempts()).isEqualTo(5);
        assertThat(credential.getLockedUntil()).isNotNull();
        verify(userCredentialRepository).update(credential);
    }

    @Test
    @DisplayName(
            "Should throw UserCredentialInvalidException when password incorrect and attempts < 5")
    void shouldThrowInvalidCredentialWhenPasswordWrongUnderThreshold() {
        UUID userId = UUID.randomUUID();
        UserCredential credential =
                UserCredential.builder()
                        .userId(userId)
                        .password("hashedPassword")
                        .failedLoginAttempts(1)
                        .lockedUntil(null)
                        .build();

        when(userCredentialRepository.findByUserId(userId)).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);
        when(i18nHelper.getMessage(any())).thenReturn("Invalid credentials");

        assertThatThrownBy(() -> service.validateAndTrackLogin(userId, "wrongPassword"))
                .isInstanceOf(UserCredentialInvalidException.class);

        assertThat(credential.getFailedLoginAttempts()).isEqualTo(2);
        verify(userCredentialRepository).update(credential);
    }

    @Test
    @DisplayName("Should unlock account and reset failed login attempts")
    void shouldUnlockAccountSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserCredential credential =
                UserCredential.builder()
                        .userId(userId)
                        .failedLoginAttempts(5)
                        .lockedUntil(LocalDateTime.now().plusMinutes(15))
                        .build();

        when(userCredentialRepository.findByUserId(userId)).thenReturn(Optional.of(credential));

        service.unlockAccount(userId);

        assertThat(credential.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(credential.getLockedUntil()).isNull();
        verify(userCredentialRepository).update(credential);
    }

    @Test
    @DisplayName("Should enable MFA with secret")
    void shouldEnableMfaSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserCredential credential =
                UserCredential.builder().userId(userId).mfaEnabled(false).build();

        when(userCredentialRepository.findByUserId(userId)).thenReturn(Optional.of(credential));

        service.updateMfa(userId, true, "SECRET123");

        assertThat(credential.getMfaEnabled()).isTrue();
        assertThat(credential.getMfaSecret()).isEqualTo("SECRET123");
        verify(userCredentialRepository).update(credential);
    }

    @Test
    @DisplayName("Should disable MFA")
    void shouldDisableMfaSuccessfully() {
        UUID userId = UUID.randomUUID();
        UserCredential credential =
                UserCredential.builder()
                        .userId(userId)
                        .mfaEnabled(true)
                        .mfaSecret("SECRET")
                        .build();

        when(userCredentialRepository.findByUserId(userId)).thenReturn(Optional.of(credential));

        service.updateMfa(userId, false, null);

        assertThat(credential.getMfaEnabled()).isFalse();
        assertThat(credential.getMfaSecret()).isNull();
        verify(userCredentialRepository).update(credential);
    }
}
