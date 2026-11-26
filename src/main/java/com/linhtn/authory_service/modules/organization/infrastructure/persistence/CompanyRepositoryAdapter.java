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

import com.linhtn.authory_service.modules.organization.domain.model.Company;
import com.linhtn.authory_service.modules.organization.domain.repository.CompanyRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.CompanyEntity;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository.CompanyJpaRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper.CompanyEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyJpaRepository companyJpaRepository;

    public CompanyRepositoryAdapter(CompanyJpaRepository companyJpaRepository) {
        this.companyJpaRepository = companyJpaRepository;
    }

    @Override
    public PagingResponse<Company> getPage(PagingRequest pagingRequest) {
        Specification<CompanyEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<CompanyEntity> entityPage = companyJpaRepository.findAll(specification, pageable);
        List<Company> items =
                entityPage.getContent().stream().map(CompanyEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Company> get(UUID id) {
        return companyJpaRepository.findById(id).map(CompanyEntityMapper::toDomain);
    }

    @Override
    public Company create(Company company) {
        CompanyEntity entity = CompanyEntityMapper.toEntity(company);
        CompanyEntity saved = companyJpaRepository.save(entity);
        return CompanyEntityMapper.toDomain(saved);
    }

    @Override
    public Company update(Company company) {
        CompanyEntity entity = CompanyEntityMapper.toEntity(company);
        CompanyEntity saved = companyJpaRepository.save(entity);
        return CompanyEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        companyJpaRepository.deleteById(id);
    }
}
