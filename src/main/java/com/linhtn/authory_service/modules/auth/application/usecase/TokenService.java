package com.linhtn.authory_service.modules.auth.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.authory_service.modules.auth.application.mapper.TokenApplicationMapper;
import com.linhtn.authory_service.modules.auth.application.port.in.TokenUseCase;
import com.linhtn.authory_service.modules.auth.domain.model.Token;
import com.linhtn.authory_service.modules.auth.domain.repository.TokenRepository;
import com.linhtn.authory_service.modules.auth.exception.error.TokenError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.TokenNotFoundException;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.web.i18n.MessageResolver;

@Service
public class TokenService implements TokenUseCase {

    private final MessageResolver i18nHelper;
    private final TokenRepository tokenRepository;
    private final TokenApplicationMapper tokenMapper;

    public TokenService(
            MessageResolver i18nHelper,
            TokenRepository tokenRepository,
            TokenApplicationMapper tokenMapper) {
        this.i18nHelper = i18nHelper;
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
    }

    @Override
    public PagingResponse<TokenResult> getPage(PagingRequest request) {
        var paged = tokenRepository.getPage(request);
        return paged.map(tokenMapper::toDto);
    }

    @Override
    public TokenResult get(UUID id) {
        return tokenMapper.toDto(
                tokenRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new TokenNotFoundException(
                                                TokenError.TOKEN_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        TokenError.TOKEN_NOT_FOUND.getMessage()))));
    }

    @Override
    @Transactional
    public TokenResult create(TokenResult request) {
        Token newToken = new Token();
        tokenMapper.fillEntity(newToken, request);
        return tokenMapper.toDto(tokenRepository.create(newToken));
    }

    @Override
    @Transactional
    public TokenResult update(UUID id, TokenResult request) {
        Token existToken =
                tokenRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new TokenNotFoundException(
                                                TokenError.TOKEN_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        TokenError.TOKEN_NOT_FOUND.getMessage())));

        tokenMapper.fillEntity(existToken, request);
        return tokenMapper.toDto(tokenRepository.update(existToken));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        tokenRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new TokenNotFoundException(
                                        TokenError.TOKEN_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                TokenError.TOKEN_NOT_FOUND.getMessage())));

        tokenRepository.delete(id);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        tokenRepository.revokeAllByUserId(userId);
    }

    @Override
    public TokenResult getByJtiAndRevokedIsFalse(String jti) {
        Token activeToken =
                tokenRepository
                        .findByJtiAndRevokedIsFalse(jti)
                        .orElseThrow(
                                () ->
                                        new TokenNotFoundException(
                                                TokenError.TOKEN_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        TokenError.TOKEN_NOT_FOUND.getMessage())));
        return tokenMapper.toDto(activeToken);
    }

    @Override
    public java.util.List<TokenResult> getActiveSessions(UUID userId) {
        return tokenRepository.findActiveSessionsByUserId(userId).stream()
                .map(tokenMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void revokeSession(UUID userId, UUID tokenId) {
        tokenRepository.revokeSession(userId, tokenId);
    }

    @Override
    @Transactional
    public void revokeAllSessionsExcept(UUID userId, UUID currentTokenId) {
        tokenRepository.revokeAllByUserIdExcept(userId, currentTokenId);
    }

    @Override
    public long countActiveSessions() {
        return tokenRepository.countByRevokedIsFalse();
    }
}
