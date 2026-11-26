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

import com.linhtn.authory_service.modules.organization.domain.model.Branch;
import com.linhtn.authory_service.modules.organization.domain.repository.BranchRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.BranchEntity;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository.BranchJpaRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper.BranchEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class BranchRepositoryAdapter implements BranchRepository {

    private final BranchJpaRepository branchJpaRepository;

    public BranchRepositoryAdapter(BranchJpaRepository branchJpaRepository) {
        this.branchJpaRepository = branchJpaRepository;
    }

    @Override
    public PagingResponse<Branch> getPage(PagingRequest pagingRequest) {
        Specification<BranchEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<BranchEntity> entityPage = branchJpaRepository.findAll(specification, pageable);
        List<Branch> items =
                entityPage.getContent().stream().map(BranchEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Branch> get(UUID id) {
        return branchJpaRepository.findById(id).map(BranchEntityMapper::toDomain);
    }

    @Override
    public Branch create(Branch branch) {
        BranchEntity entity = BranchEntityMapper.toEntity(branch);
        BranchEntity saved = branchJpaRepository.save(entity);
        return BranchEntityMapper.toDomain(saved);
    }

    @Override
    public Branch update(Branch branch) {
        BranchEntity entity = BranchEntityMapper.toEntity(branch);
        BranchEntity saved = branchJpaRepository.save(entity);
        return BranchEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        branchJpaRepository.deleteById(id);
    }
}
