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

import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.authory_service.modules.rbac.domain.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.ResourceEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.ResourceJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.ResourceEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class ResourceRepositoryAdapter implements ResourceRepository {

    private final ResourceJpaRepository resourceJpaRepository;

    public ResourceRepositoryAdapter(ResourceJpaRepository resourceJpaRepository) {
        this.resourceJpaRepository = resourceJpaRepository;
    }

    @Override
    public PagingResponse<Resource> getPage(PagingRequest pagingRequest) {
        Specification<ResourceEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<ResourceEntity> entityPage = resourceJpaRepository.findAll(specification, pageable);
        List<Resource> items =
                entityPage.getContent().stream().map(ResourceEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Resource> get(UUID id) {
        return resourceJpaRepository.findById(id).map(ResourceEntityMapper::toDomain);
    }

    @Override
    public Resource create(Resource resource) {
        ResourceEntity entity = ResourceEntityMapper.toEntity(resource);
        ResourceEntity saved = resourceJpaRepository.save(entity);
        return ResourceEntityMapper.toDomain(saved);
    }

    @Override
    public Resource update(Resource resource) {
        ResourceEntity entity = ResourceEntityMapper.toEntity(resource);
        ResourceEntity saved = resourceJpaRepository.save(entity);
        return ResourceEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        resourceJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return resourceJpaRepository.existsById(id);
    }

    @Override
    public List<Resource> findAllById(Iterable<UUID> ids) {
        return resourceJpaRepository.findAllById(ids).stream()
                .map(ResourceEntityMapper::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return resourceJpaRepository.count();
    }
}
