package com.linhtn.authory_service.modules.auth.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.auth.domain.model.Token;
import com.linhtn.authory_service.modules.auth.domain.repository.TokenRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.TokenEntity;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository.TokenJpaRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper.TokenEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class TokenRepositoryAdapter implements TokenRepository {

    private final TokenJpaRepository tokenJpaRepository;

    public TokenRepositoryAdapter(TokenJpaRepository tokenJpaRepository) {
        this.tokenJpaRepository = tokenJpaRepository;
    }

    @Override
    public PagingResponse<Token> getPage(PagingRequest pagingRequest) {
        Specification<TokenEntity> specification = new GenericSpecification<>(pagingRequest);

        List<Sort.Order> orders = new ArrayList<>();
        if (pagingRequest.getOrders() != null) {
            pagingRequest
                    .getOrders()
                    .forEach(
                            o -> {
                                if (o.getSort() == FieldOrder.SortType.asc) {
                                    orders.add(Sort.Order.asc(o.getField()));
                                } else if (o.getSort() == FieldOrder.SortType.desc) {
                                    orders.add(Sort.Order.desc(o.getField()));
                                }
                            });
        }
        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        Pageable pageable =
                PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize(), sort);

        Page<TokenEntity> entityPage = tokenJpaRepository.findAll(specification, pageable);
        List<Token> items =
                entityPage.getContent().stream().map(TokenEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Token> get(UUID id) {
        return tokenJpaRepository.findById(id).map(TokenEntityMapper::toDomain);
    }

    @Override
    public Token create(Token token) {
        TokenEntity entity = TokenEntityMapper.toEntity(token);
        TokenEntity saved = tokenJpaRepository.save(entity);
        return TokenEntityMapper.toDomain(saved);
    }

    @Override
    public Token update(Token token) {
        TokenEntity entity = TokenEntityMapper.toEntity(token);
        TokenEntity saved = tokenJpaRepository.save(entity);
        return TokenEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        tokenJpaRepository.deleteById(id);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        tokenJpaRepository.revokeAllByUserId(userId);
    }

    @Override
    public void revokeAllByUserIdExcept(UUID userId, UUID excludedTokenId) {
        tokenJpaRepository.revokeAllByUserIdExcept(userId, excludedTokenId);
    }

    @Override
    public void revokeSession(UUID userId, UUID tokenId) {
        tokenJpaRepository.revokeSession(userId, tokenId);
    }

    @Override
    public List<Token> findActiveSessionsByUserId(UUID userId) {
        return tokenJpaRepository.findByUserIdAndTypeAndRevokedIsFalse(userId, "REFRESH").stream()
                .map(TokenEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Token> findByJtiAndRevokedIsFalse(String jti) {
        return tokenJpaRepository.findByJtiAndRevokedIsFalse(jti).map(TokenEntityMapper::toDomain);
    }

    @Override
    public long countByRevokedIsFalse() {
        return tokenJpaRepository.countByRevokedIsFalse();
    }
}
