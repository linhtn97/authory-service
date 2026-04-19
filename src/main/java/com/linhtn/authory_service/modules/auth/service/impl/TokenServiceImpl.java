package com.linhtn.authory_service.modules.auth.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.auth.domain.dto.TokenDto;
import com.linhtn.authory_service.modules.auth.domain.entity.Token;
import com.linhtn.authory_service.modules.auth.domain.mapper.TokenMapper;
import com.linhtn.authory_service.modules.auth.exception.error.TokenError;
import com.linhtn.authory_service.modules.auth.exception.unchecked.TokenNotFoundException;
import com.linhtn.authory_service.modules.auth.repository.TokenRepository;
import com.linhtn.authory_service.modules.auth.service.TokenService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final I18nHelper i18nHelper;
    private final TokenRepository tokenRepository;
    private final TokenMapper tokenMapper;

    public TokenServiceImpl(I18nHelper i18nHelper,
                            TokenRepository tokenRepository,
                            TokenMapper tokenMapper) {
        this.i18nHelper = i18nHelper;
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
    }

    @Override
    public PagingResponse<TokenDto> getPage(PagingRequest request) {
        Specification<Token> specification = new GenericSpecification<>(request);

        List<Sort.Order> orders = new ArrayList<>();
        request.getOrders().forEach(o -> {
            if (o.getSort().equals(FieldOrder.SortType.asc)) {
                orders.add(Sort.Order.asc(o.getField()));
            } else if (o.getSort().equals(FieldOrder.SortType.desc)) {
                orders.add(Sort.Order.desc(o.getField()));
            }
        });

        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sort);

        var paging = tokenRepository.findAll(specification, pageable);

        return tokenMapper.toPage(paging);
    }

    @Override
    public TokenDto get(UUID id) {
        return tokenMapper.toDto(
                tokenRepository.findById(id).orElseThrow(() ->
                        new TokenNotFoundException(
                                TokenError.TOKEN_NOT_FOUND.name(),
                                i18nHelper.getMessage(TokenError.TOKEN_NOT_FOUND.getMessage())
                        )
                )
        );
    }

    @Override
    @Transactional
    public TokenDto create(TokenDto request) {
        Token newToken = new Token();
        tokenMapper.fillEntity(newToken, request);
        return tokenMapper.toDto(tokenRepository.save(newToken));
    }

    @Override
    @Transactional
    public TokenDto update(UUID id, TokenDto request) {
        Token existToken = tokenRepository.findById(id).orElseThrow(() ->
                new TokenNotFoundException(
                        TokenError.TOKEN_NOT_FOUND.name(),
                        i18nHelper.getMessage(TokenError.TOKEN_NOT_FOUND.getMessage())
                )
        );

        tokenMapper.fillEntity(existToken, request);
        return tokenMapper.toDto(tokenRepository.save(existToken));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Token existToken = tokenRepository.findById(id).orElseThrow(() ->
                new TokenNotFoundException(
                        TokenError.TOKEN_NOT_FOUND.name(),
                        i18nHelper.getMessage(TokenError.TOKEN_NOT_FOUND.getMessage())
                )
        );

        tokenRepository.delete(existToken);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        tokenRepository.revokeAllByUserId(userId);
    }

    @Override
    public TokenDto getByJtiAndRevokedIsFalse(String jti) {
        Token activeToken = tokenRepository.findByJtiAndRevokedIsFalse(jti).orElseThrow(() ->
                new TokenNotFoundException(
                        TokenError.TOKEN_NOT_FOUND.name(),
                        i18nHelper.getMessage(TokenError.TOKEN_NOT_FOUND.getMessage())
                )
        );
        return tokenMapper.toDto(activeToken);
    }
}
