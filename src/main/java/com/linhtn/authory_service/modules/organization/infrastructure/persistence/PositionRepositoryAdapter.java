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

import com.linhtn.authory_service.modules.organization.domain.model.Position;
import com.linhtn.authory_service.modules.organization.domain.repository.PositionRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.PositionEntity;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository.PositionJpaRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper.PositionEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class PositionRepositoryAdapter implements PositionRepository {

    private final PositionJpaRepository positionJpaRepository;

    public PositionRepositoryAdapter(PositionJpaRepository positionJpaRepository) {
        this.positionJpaRepository = positionJpaRepository;
    }

    @Override
    public PagingResponse<Position> getPage(PagingRequest pagingRequest) {
        Specification<PositionEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<PositionEntity> entityPage = positionJpaRepository.findAll(specification, pageable);
        List<Position> items =
                entityPage.getContent().stream().map(PositionEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Position> get(UUID id) {
        return positionJpaRepository.findById(id).map(PositionEntityMapper::toDomain);
    }

    @Override
    public Position create(Position position) {
        PositionEntity entity = PositionEntityMapper.toEntity(position);
        PositionEntity saved = positionJpaRepository.save(entity);
        return PositionEntityMapper.toDomain(saved);
    }

    @Override
    public Position update(Position position) {
        PositionEntity entity = PositionEntityMapper.toEntity(position);
        PositionEntity saved = positionJpaRepository.save(entity);
        return PositionEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        positionJpaRepository.deleteById(id);
    }
}
