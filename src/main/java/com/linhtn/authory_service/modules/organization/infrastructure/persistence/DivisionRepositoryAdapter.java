package com.linhtn.authory_service.modules.organization.infrastructure.persistence;

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

import com.linhtn.authory_service.modules.organization.domain.model.Division;
import com.linhtn.authory_service.modules.organization.domain.repository.DivisionRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.DivisionEntity;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository.DivisionJpaRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper.DivisionEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class DivisionRepositoryAdapter implements DivisionRepository {

    private final DivisionJpaRepository divisionJpaRepository;

    public DivisionRepositoryAdapter(DivisionJpaRepository divisionJpaRepository) {
        this.divisionJpaRepository = divisionJpaRepository;
    }

    @Override
    public PagingResponse<Division> getPage(PagingRequest pagingRequest) {
        Specification<DivisionEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<DivisionEntity> entityPage = divisionJpaRepository.findAll(specification, pageable);
        List<Division> items =
                entityPage.getContent().stream().map(DivisionEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Division> get(UUID id) {
        return divisionJpaRepository.findById(id).map(DivisionEntityMapper::toDomain);
    }

    @Override
    public Division create(Division division) {
        DivisionEntity entity = DivisionEntityMapper.toEntity(division);
        DivisionEntity saved = divisionJpaRepository.save(entity);
        return DivisionEntityMapper.toDomain(saved);
    }

    @Override
    public Division update(Division division) {
        DivisionEntity entity = DivisionEntityMapper.toEntity(division);
        DivisionEntity saved = divisionJpaRepository.save(entity);
        return DivisionEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        divisionJpaRepository.deleteById(id);
    }
}
