package com.linhtn.authory_service.modules.user.application.port.in;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;

public interface UserGetUseCase {
    UserGetResult execute(UUID id);

    Optional<UserGetResult> findById(UUID id);

    UserGetResult getByUsername(String username);

    Optional<UserGetResult> findByEmail(String email);

    long count();

    long countNewUsersToday();
}
