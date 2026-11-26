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

import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.entity.AttributeEntity;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.repository.AttributeJpaRepository;
import com.linhtn.authory_service.modules.abac.infrastructure.persistence.mapper.AttributeEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class AttributeRepositoryAdapter implements AttributeRepository {

    private final AttributeJpaRepository attributeJpaRepository;

    public AttributeRepositoryAdapter(AttributeJpaRepository attributeJpaRepository) {
        this.attributeJpaRepository = attributeJpaRepository;
    }

    @Override
    public PagingResponse<Attribute> getPage(PagingRequest pagingRequest) {
        Specification<AttributeEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<AttributeEntity> entityPage = attributeJpaRepository.findAll(specification, pageable);
        List<Attribute> items =
                entityPage.getContent().stream().map(AttributeEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Attribute> get(UUID id) {
        return attributeJpaRepository.findById(id).map(AttributeEntityMapper::toDomain);
    }

    @Override
    public Attribute create(Attribute attribute) {
        AttributeEntity entity = AttributeEntityMapper.toEntity(attribute);
        AttributeEntity saved = attributeJpaRepository.save(entity);
        return AttributeEntityMapper.toDomain(saved);
    }

    @Override
    public Attribute update(Attribute attribute) {
        AttributeEntity entity = AttributeEntityMapper.toEntity(attribute);
        AttributeEntity saved = attributeJpaRepository.save(entity);
        return AttributeEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        attributeJpaRepository.deleteById(id);
    }
}
