package com.linhtn.authory_service.modules.user.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.application.port.in.UserCredentialUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserUnlockUseCase;

@Service
public class UserUnlockService implements UserUnlockUseCase {

    private final UserCredentialUseCase userCredentialUseCase;

    public UserUnlockService(UserCredentialUseCase userCredentialUseCase) {
        this.userCredentialUseCase = userCredentialUseCase;
    }

    @Override
    @Transactional
    public void execute(UUID userId) {
        userCredentialUseCase.unlockAccount(userId);
    }
}
