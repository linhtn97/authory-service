package com.linhtn.authory_service.modules.abac.service;

import com.linhtn.authory_service.modules.abac.domain.dto.UserAttributeDto;
import com.linhtn.shared_kernel.domain.service.BaseCrudService;

import java.util.UUID;

public interface UserAttributeService extends BaseCrudService<UserAttributeDto> {
    void add(UUID userId, UserAttributeDto attribute);
}
