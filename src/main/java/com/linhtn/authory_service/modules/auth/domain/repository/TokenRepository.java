package com.linhtn.authory_service.modules.auth.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.auth.domain.model.Token;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface TokenRepository {
    PagingResponse<Token> getPage(PagingRequest pagingRequest);

    Optional<Token> get(UUID id);

    Token create(Token token);

    Token update(Token token);

    void delete(UUID id);

    void revokeAllByUserId(UUID userId);

    void revokeAllByUserIdExcept(UUID userId, UUID excludedTokenId);

    void revokeSession(UUID userId, UUID tokenId);

    java.util.List<Token> findActiveSessionsByUserId(UUID userId);

    Optional<Token> findByJtiAndRevokedIsFalse(String jti);

    long countByRevokedIsFalse();
}
