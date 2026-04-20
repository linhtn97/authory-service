package com.linhtn.authory_service.modules.auth.service;

import java.util.UUID;

import com.linhtn.authory_service.modules.auth.domain.dto.TokenDto;
import com.linhtn.shared_kernel.domain.service.BaseCrudService;

public interface TokenService extends BaseCrudService<TokenDto> {
    void revokeAllByUserId(UUID userId);

    TokenDto getByJtiAndRevokedIsFalse(String jti);
}
