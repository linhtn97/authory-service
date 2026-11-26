package com.linhtn.authory_service.modules.abac.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeGetResult;

public interface AttributeGetUseCase {
    AttributeGetResult execute(UUID id);
}
