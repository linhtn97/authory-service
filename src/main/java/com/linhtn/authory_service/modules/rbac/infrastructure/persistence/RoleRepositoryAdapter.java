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

import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.RoleEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.RoleJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.RoleEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public PagingResponse<Role> getPage(PagingRequest pagingRequest) {
        Specification<RoleEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<RoleEntity> entityPage = roleJpaRepository.findAll(specification, pageable);
        List<Role> items =
                entityPage.getContent().stream().map(RoleEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Role> get(UUID id) {
        return roleJpaRepository.findById(id).map(RoleEntityMapper::toDomain);
    }

    @Override
    public Role create(Role role) {
        RoleEntity entity = RoleEntityMapper.toEntity(role);
        RoleEntity saved = roleJpaRepository.save(entity);
        return RoleEntityMapper.toDomain(saved);
    }

    @Override
    public Role update(Role role) {
        RoleEntity entity = RoleEntityMapper.toEntity(role);
        RoleEntity saved = roleJpaRepository.save(entity);
        return RoleEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        roleJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return roleJpaRepository.existsById(id);
    }

    @Override
    public List<Role> findAllById(Iterable<UUID> ids) {
        return roleJpaRepository.findAllById(ids).stream().map(RoleEntityMapper::toDomain).toList();
    }

    @Override
    public long count() {
        return roleJpaRepository.count();
    }
}
