package com.linhtn.authory_service.modules.user.application.port.in;

import com.linhtn.authory_service.modules.user.application.dto.command.UserCreateCommand;
import com.linhtn.authory_service.modules.user.application.dto.result.UserCreateResult;

public interface UserCreateUseCase {
    UserCreateResult execute(UserCreateCommand command);
}
