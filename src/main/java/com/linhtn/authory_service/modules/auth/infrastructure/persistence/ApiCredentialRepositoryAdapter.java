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

import com.linhtn.authory_service.modules.auth.domain.model.ApiCredential;
import com.linhtn.authory_service.modules.auth.domain.repository.ApiCredentialRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.ApiCredentialEntity;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository.ApiCredentialJpaRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper.ApiCredentialEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class ApiCredentialRepositoryAdapter implements ApiCredentialRepository {

    private final ApiCredentialJpaRepository apiCredentialJpaRepository;

    public ApiCredentialRepositoryAdapter(ApiCredentialJpaRepository apiCredentialJpaRepository) {
        this.apiCredentialJpaRepository = apiCredentialJpaRepository;
    }

    @Override
    public PagingResponse<ApiCredential> getPage(PagingRequest pagingRequest) {
        Specification<ApiCredentialEntity> specification =
                new GenericSpecification<>(pagingRequest);

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

        Page<ApiCredentialEntity> entityPage =
                apiCredentialJpaRepository.findAll(specification, pageable);
        List<ApiCredential> items =
                entityPage.getContent().stream().map(ApiCredentialEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<ApiCredential> get(UUID id) {
        return apiCredentialJpaRepository.findById(id).map(ApiCredentialEntityMapper::toDomain);
    }

    @Override
    public ApiCredential create(ApiCredential credential) {
        ApiCredentialEntity entity = ApiCredentialEntityMapper.toEntity(credential);
        ApiCredentialEntity saved = apiCredentialJpaRepository.save(entity);
        return ApiCredentialEntityMapper.toDomain(saved);
    }

    @Override
    public ApiCredential update(ApiCredential credential) {
        ApiCredentialEntity entity = ApiCredentialEntityMapper.toEntity(credential);
        ApiCredentialEntity saved = apiCredentialJpaRepository.save(entity);
        return ApiCredentialEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        apiCredentialJpaRepository.deleteById(id);
    }

    @Override
    public Optional<ApiCredential> findByClientIdAndHashedSecret(
            String clientId, String hashedSecret) {
        return apiCredentialJpaRepository
                .findByClientIdAndHashedSecret(clientId, hashedSecret)
                .map(ApiCredentialEntityMapper::toDomain);
    }

    @Override
    public Optional<ApiCredential> findByApiKey(String apiKey) {
        return apiCredentialJpaRepository
                .findByApiKey(apiKey)
                .map(ApiCredentialEntityMapper::toDomain);
    }

    @Override
    public Optional<ApiCredential> findByUserId(UUID userId) {
        return apiCredentialJpaRepository
                .findByUserId(userId)
                .map(ApiCredentialEntityMapper::toDomain);
    }

    @Override
    public List<ApiCredential> findAllByUserId(UUID userId) {
        return apiCredentialJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ApiCredentialEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ApiCredential> findByIdAndUserId(UUID id, UUID userId) {
        return apiCredentialJpaRepository
                .findByIdAndUserId(id, userId)
                .map(ApiCredentialEntityMapper::toDomain);
    }
}
