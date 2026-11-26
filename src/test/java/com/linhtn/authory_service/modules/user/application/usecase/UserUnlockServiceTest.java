package com.linhtn.authory_service.modules.user.application.usecase;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.auth.application.port.in.UserCredentialUseCase;

@ExtendWith(MockitoExtension.class)
class UserUnlockServiceTest {

    @Mock private UserCredentialUseCase userCredentialUseCase;

    private UserUnlockService service;

    @BeforeEach
    void setUp() {
        service = new UserUnlockService(userCredentialUseCase);
    }

    @Test
    @DisplayName("Should delegate unlock request to UserCredentialUseCase")
    void shouldDelegateUnlock() {
        UUID userId = UUID.randomUUID();

        service.execute(userId);

        verify(userCredentialUseCase).unlockAccount(userId);
    }
}
