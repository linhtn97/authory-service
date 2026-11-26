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

import com.linhtn.authory_service.modules.organization.domain.model.Department;
import com.linhtn.authory_service.modules.organization.domain.repository.DepartmentRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.DepartmentEntity;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository.DepartmentJpaRepository;
import com.linhtn.authory_service.modules.organization.infrastructure.persistence.mapper.DepartmentEntityMapper;
import com.linhtn.data_jpa.specification.GenericSpecification;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;
import com.linhtn.sharedkernel.query.FieldOrder;

@Repository
public class DepartmentRepositoryAdapter implements DepartmentRepository {

    private final DepartmentJpaRepository departmentJpaRepository;

    public DepartmentRepositoryAdapter(DepartmentJpaRepository departmentJpaRepository) {
        this.departmentJpaRepository = departmentJpaRepository;
    }

    @Override
    public PagingResponse<Department> getPage(PagingRequest pagingRequest) {
        Specification<DepartmentEntity> specification = new GenericSpecification<>(pagingRequest);

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

        Page<DepartmentEntity> entityPage =
                departmentJpaRepository.findAll(specification, pageable);
        List<Department> items =
                entityPage.getContent().stream().map(DepartmentEntityMapper::toDomain).toList();

        return PagingResponse.of(
                items,
                pagingRequest.getPage(),
                pagingRequest.getPageSize(),
                entityPage.getTotalElements());
    }

    @Override
    public Optional<Department> get(UUID id) {
        return departmentJpaRepository.findById(id).map(DepartmentEntityMapper::toDomain);
    }

    @Override
    public Department create(Department department) {
        DepartmentEntity entity = DepartmentEntityMapper.toEntity(department);
        DepartmentEntity saved = departmentJpaRepository.save(entity);
        return DepartmentEntityMapper.toDomain(saved);
    }

    @Override
    public Department update(Department department) {
        DepartmentEntity entity = DepartmentEntityMapper.toEntity(department);
        DepartmentEntity saved = departmentJpaRepository.save(entity);
        return DepartmentEntityMapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        departmentJpaRepository.deleteById(id);
    }
}
