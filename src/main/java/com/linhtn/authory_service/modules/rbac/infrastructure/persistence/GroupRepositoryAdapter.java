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

import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.GroupEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.GroupJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.GroupEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class GroupRepositoryAdapter implements GroupRepository {

    private final GroupJpaRepository groupJpaRepository;

    public GroupRepositoryAdapter(GroupJpaRepository groupJpaRepository) {
        this.groupJpaRepository = groupJpaRepository;
    }

    @Override
    public PagingResponse<Group> getPage(PagingRequest pagingRequest) {
        Specification<GroupEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<GroupEntity> entityPage = groupJpaRepository.findAll(specification, pageable);
        List<Group> items =
                entityPage.getContent().stream().map(GroupEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Group> get(UUID id) {
        return groupJpaRepository.findById(id).map(GroupEntityMapper::toDomain);
    }

    @Override
    public Group create(Group group) {
        GroupEntity entity = GroupEntityMapper.toEntity(group);
        GroupEntity saved = groupJpaRepository.save(entity);
        return GroupEntityMapper.toDomain(saved);
    }

    @Override
    public Group update(Group group) {
        GroupEntity entity = GroupEntityMapper.toEntity(group);
        GroupEntity saved = groupJpaRepository.save(entity);
        return GroupEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        groupJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return groupJpaRepository.existsById(id);
    }

    @Override
    public List<Group> findAllById(Iterable<UUID> ids) {
        return groupJpaRepository.findAllById(ids).stream()
                .map(GroupEntityMapper::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return groupJpaRepository.count();
    }
}
