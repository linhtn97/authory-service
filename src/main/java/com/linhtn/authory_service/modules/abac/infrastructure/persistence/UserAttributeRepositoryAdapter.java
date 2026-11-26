package com.linhtn.authory_service.modules.abac.infrastructure.persistence;

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

import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.entity.UserAttributeEntity;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.repository.UserAttributeJpaRepository;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.mapper.UserAttributeEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class UserAttributeRepositoryAdapter implements UserAttributeRepository {

    private final UserAttributeJpaRepository userAttributeJpaRepository;

    public UserAttributeRepositoryAdapter(UserAttributeJpaRepository userAttributeJpaRepository) {
        this.userAttributeJpaRepository = userAttributeJpaRepository;
    }

    @Override
    public PagingResponse<UserAttribute> getPage(PagingRequest pagingRequest) {
        Specification<UserAttributeEntity> specification =
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

        Page<UserAttributeEntity> entityPage =
                userAttributeJpaRepository.findAll(specification, pageable);
        List<UserAttribute> items =
                entityPage.getContent().stream().map(UserAttributeEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<UserAttribute> get(UUID id) {
        return userAttributeJpaRepository.findById(id).map(UserAttributeEntityMapper::toDomain);
    }

    @Override
    public UserAttribute create(UserAttribute userAttribute) {
        UserAttributeEntity entity = UserAttributeEntityMapper.toEntity(userAttribute);
        UserAttributeEntity saved = userAttributeJpaRepository.save(entity);
        return UserAttributeEntityMapper.toDomain(saved);
    }

    @Override
    public UserAttribute update(UserAttribute userAttribute) {
        UserAttributeEntity entity = UserAttributeEntityMapper.toEntity(userAttribute);
        UserAttributeEntity saved = userAttributeJpaRepository.save(entity);
        return UserAttributeEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        userAttributeJpaRepository.deleteById(id);
    }

    @Override
    public List<UserAttribute> findByUserId(UUID userId) {
        return userAttributeJpaRepository.findByUserId(userId).stream()
                .map(UserAttributeEntityMapper::toDomain)
                .toList();
    }
}
