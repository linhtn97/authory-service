package com.linhtn.authory_service.modules.auth.application.port.in;

import java.util.List;
import java.util.UUID;

import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface TokenUseCase {
    PagingResponse<TokenResult> getPage(PagingRequest request);

    TokenResult get(UUID id);

    TokenResult create(TokenResult request);

    TokenResult update(UUID id, TokenResult request);

    void delete(UUID id);

    void revokeAllByUserId(UUID userId);

    TokenResult getByJtiAndRevokedIsFalse(String jti);

    List<TokenResult> getActiveSessions(UUID userId);

    void revokeSession(UUID userId, UUID tokenId);

    void revokeAllSessionsExcept(UUID userId, UUID currentTokenId);

    long countActiveSessions();
}
