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

import com.linhtn.authory_service.modules.auth.domain.model.UserCredential;
import com.linhtn.authory_service.modules.auth.domain.repository.UserCredentialRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.UserCredentialEntity;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository.UserCredentialJpaRepository;
import com.linhtn.authory_service.modules.auth.infrastructure.persistence.mapper.UserCredentialEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class UserCredentialRepositoryAdapter implements UserCredentialRepository {

    private final UserCredentialJpaRepository userCredentialJpaRepository;

    public UserCredentialRepositoryAdapter(
            UserCredentialJpaRepository userCredentialJpaRepository) {
        this.userCredentialJpaRepository = userCredentialJpaRepository;
    }

    @Override
    public PagingResponse<UserCredential> getPage(PagingRequest pagingRequest) {
        Specification<UserCredentialEntity> specification =
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

        Page<UserCredentialEntity> entityPage =
                userCredentialJpaRepository.findAll(specification, pageable);
        List<UserCredential> items =
                entityPage.getContent().stream().map(UserCredentialEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<UserCredential> get(UUID id) {
        return userCredentialJpaRepository.findById(id).map(UserCredentialEntityMapper::toDomain);
    }

    @Override
    public UserCredential create(UserCredential userCredential) {
        UserCredentialEntity entity = UserCredentialEntityMapper.toEntity(userCredential);
        UserCredentialEntity saved = userCredentialJpaRepository.save(entity);
        return UserCredentialEntityMapper.toDomain(saved);
    }

    @Override
    public UserCredential update(UserCredential userCredential) {
        UserCredentialEntity entity = UserCredentialEntityMapper.toEntity(userCredential);
        UserCredentialEntity saved = userCredentialJpaRepository.save(entity);
        return UserCredentialEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        userCredentialJpaRepository.deleteById(id);
    }

    @Override
    public Optional<UserCredential> findByUserId(UUID userId) {
        return userCredentialJpaRepository
                .findByUserId(userId)
                .map(UserCredentialEntityMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndPassword(UUID userId, String password) {
        return userCredentialJpaRepository.existsByUserIdAndPassword(userId, password);
    }
}
