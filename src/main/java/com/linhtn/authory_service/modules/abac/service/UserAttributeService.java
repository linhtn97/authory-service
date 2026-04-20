package com.linhtn.authory_service.modules.abac.service;

import java.util.UUID;

import com.linhtn.authory_service.modules.abac.domain.dto.UserAttributeDto;
import com.linhtn.shared_kernel.domain.service.BaseCrudService;

public interface UserAttributeService extends BaseCrudService<UserAttributeDto> {
    void add(UUID userId, UserAttributeDto attribute);
}
