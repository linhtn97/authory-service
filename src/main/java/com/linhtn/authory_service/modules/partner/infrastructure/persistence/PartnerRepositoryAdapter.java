package com.linhtn.authory_service.modules.partner.infrastructure.persistence;

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

import com.linhtn.authory_service.modules.partner.domain.model.Partner;
import com.linhtn.authory_service.modules.partner.domain.repository.PartnerRepository;
import com.linhtn.authory_service.modules.partner.infrastructure.persistence.jpa.entity.PartnerEntity;
import com.linhtn.authory_service.modules.partner.infrastructure.persistence.jpa.repository.PartnerJpaRepository;
import com.linhtn.authory_service.modules.partner.infrastructure.persistence.mapper.PartnerEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class PartnerRepositoryAdapter implements PartnerRepository {

    private final PartnerJpaRepository partnerJpaRepository;

    public PartnerRepositoryAdapter(PartnerJpaRepository partnerJpaRepository) {
        this.partnerJpaRepository = partnerJpaRepository;
    }

    @Override
    public PagingResponse<Partner> getPage(PagingRequest pagingRequest) {
        Specification<PartnerEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<PartnerEntity> entityPage = partnerJpaRepository.findAll(specification, pageable);
        List<Partner> items =
                entityPage.getContent().stream().map(PartnerEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Partner> get(UUID id) {
        return partnerJpaRepository.findById(id).map(PartnerEntityMapper::toDomain);
    }

    @Override
    public Partner create(Partner partner) {
        PartnerEntity entity = PartnerEntityMapper.toEntity(partner);
        PartnerEntity saved = partnerJpaRepository.save(entity);
        return PartnerEntityMapper.toDomain(saved);
    }

    @Override
    public Partner update(Partner partner) {
        PartnerEntity entity = PartnerEntityMapper.toEntity(partner);
        PartnerEntity saved = partnerJpaRepository.save(entity);
        return PartnerEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        partnerJpaRepository.deleteById(id);
    }
}
