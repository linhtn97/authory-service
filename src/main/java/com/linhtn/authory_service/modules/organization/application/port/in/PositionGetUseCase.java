package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.result.PositionGetResult;

public interface PositionGetUseCase {
    PositionGetResult execute(UUID id);
}
