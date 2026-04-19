package com.linhtn.authory_service.modules.auth.service;

import com.linhtn.authory_service.modules.auth.domain.dto.UserCredentialDto;
import com.linhtn.shared_kernel.domain.service.BaseCrudService;

import java.util.UUID;

public interface UserCredentialService extends BaseCrudService<UserCredentialDto> {
    UserCredentialDto getByUserId(UUID userId);
    boolean existsByUserIdAndPassword(UUID userId, String password);
}
