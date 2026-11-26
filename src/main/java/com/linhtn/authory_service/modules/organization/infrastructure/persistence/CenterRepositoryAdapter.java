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

import com.linhtn.authory_service.modules.organization.domain.model.Center;
import com.linhtn.authory_service.modules.organization.domain.repository.CenterRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.CenterEntity;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository.CenterJpaRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper.CenterEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class CenterRepositoryAdapter implements CenterRepository {

    private final CenterJpaRepository centerJpaRepository;

    public CenterRepositoryAdapter(CenterJpaRepository centerJpaRepository) {
        this.centerJpaRepository = centerJpaRepository;
    }

    @Override
    public PagingResponse<Center> getPage(PagingRequest pagingRequest) {
        Specification<CenterEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<CenterEntity> entityPage = centerJpaRepository.findAll(specification, pageable);
        List<Center> items =
                entityPage.getContent().stream().map(CenterEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Center> get(UUID id) {
        return centerJpaRepository.findById(id).map(CenterEntityMapper::toDomain);
    }

    @Override
    public Center create(Center center) {
        CenterEntity entity = CenterEntityMapper.toEntity(center);
        CenterEntity saved = centerJpaRepository.save(entity);
        return CenterEntityMapper.toDomain(saved);
    }

    @Override
    public Center update(Center center) {
        CenterEntity entity = CenterEntityMapper.toEntity(center);
        CenterEntity saved = centerJpaRepository.save(entity);
        return CenterEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        centerJpaRepository.deleteById(id);
    }
}
