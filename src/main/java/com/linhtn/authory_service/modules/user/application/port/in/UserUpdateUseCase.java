package com.linhtn.authory_service.modules.user.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.user.application.dto.command.UserUpdateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserUpdateResult;

public interface UserUpdateUseCase {
    UserUpdateResult execute(UUID id, UserUpdateCommand command);
}
