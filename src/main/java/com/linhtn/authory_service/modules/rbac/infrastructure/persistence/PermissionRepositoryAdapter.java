package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

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

import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.PermissionEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.PermissionJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.PermissionEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository permissionJpaRepository;

    public PermissionRepositoryAdapter(PermissionJpaRepository permissionJpaRepository) {
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public PagingResponse<Permission> getPage(PagingRequest pagingRequest) {
        Specification<PermissionEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<PermissionEntity> entityPage =
                permissionJpaRepository.findAll(specification, pageable);
        List<Permission> items =
                entityPage.getContent().stream().map(PermissionEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Permission> get(UUID id) {
        return permissionJpaRepository.findById(id).map(PermissionEntityMapper::toDomain);
    }

    @Override
    public Permission create(Permission permission) {
        PermissionEntity entity = PermissionEntityMapper.toEntity(permission);
        PermissionEntity saved = permissionJpaRepository.save(entity);
        return PermissionEntityMapper.toDomain(saved);
    }

    @Override
    public Permission update(Permission permission) {
        PermissionEntity entity = PermissionEntityMapper.toEntity(permission);
        PermissionEntity saved = permissionJpaRepository.save(entity);
        return PermissionEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        permissionJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return permissionJpaRepository.existsById(id);
    }

    @Override
    public List<Permission> findAllById(Iterable<UUID> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(PermissionEntityMapper::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return permissionJpaRepository.count();
    }
}
