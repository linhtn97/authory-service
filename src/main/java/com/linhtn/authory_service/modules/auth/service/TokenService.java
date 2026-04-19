package com.linhtn.authory_service.modules.auth.service;

import com.linhtn.authory_service.modules.auth.domain.dto.TokenDto;
import com.linhtn.shared_kernel.domain.service.BaseCrudService;

import java.util.UUID;

public interface TokenService extends BaseCrudService<TokenDto> {
    void revokeAllByUserId(UUID userId);
    TokenDto getByJtiAndRevokedIsFalse(String jti);
}
