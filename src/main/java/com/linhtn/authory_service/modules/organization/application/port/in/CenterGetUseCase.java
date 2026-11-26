package com.linhtn.authory_service.modules.organization.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.organization.application.dto.result.CenterGetResult;

public interface CenterGetUseCase {
    CenterGetResult execute(UUID id);
}
