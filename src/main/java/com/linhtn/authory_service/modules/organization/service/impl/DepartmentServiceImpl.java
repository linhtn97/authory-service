package com.linhtn.authory_service.modules.organization.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.organization.domain.dto.DepartmentDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Department;
import com.linhtn.authory_service.modules.organization.domain.mapper.DepartmentMapper;
import com.linhtn.authory_service.modules.organization.exception.error.DepartmentError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.DepartmentNotFoundException;
import com.linhtn.authory_service.modules.organization.repository.DepartmentRepository;
import com.linhtn.authory_service.modules.organization.service.DepartmentService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final I18nHelper i18nHelper;
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(
            I18nHelper i18nHelper,
            DepartmentRepository departmentRepository,
            DepartmentMapper departmentMapper) {
        this.i18nHelper = i18nHelper;
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public PagingResponse<DepartmentDto> getPage(PagingRequest request) {
        Specification<Department> specification = new GenericSpecification<>(request);

        List<Sort.Order> orders = new ArrayList<>();
        request.getOrders()
                .forEach(
                        o -> {
                            if (o.getSort().equals(FieldOrder.SortType.asc)) {
                                orders.add(Sort.Order.asc(o.getField()));
                            } else if (o.getSort().equals(FieldOrder.SortType.desc)) {
                                orders.add(Sort.Order.desc(o.getField()));
                            }
                        });
        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sort);

        var paging = departmentRepository.findAll(specification, pageable);

        return departmentMapper.toPage(paging);
    }

    @Override
    public DepartmentDto get(UUID id) {
        return departmentMapper.toDto(
                departmentRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new DepartmentNotFoundException(
                                                DepartmentError.DEPARTMENT_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        DepartmentError.DEPARTMENT_NOT_FOUND
                                                                .getMessage()))));
    }

    @Override
    @Transactional
    public DepartmentDto create(DepartmentDto request) {
        var newDepartment = new Department();
        departmentMapper.fillEntity(newDepartment, request);
        return departmentMapper.toDto(departmentRepository.save(newDepartment));
    }

    @Override
    @Transactional
    public DepartmentDto update(UUID id, DepartmentDto request) {
        Department existDepartment =
                departmentRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new DepartmentNotFoundException(
                                                DepartmentError.DEPARTMENT_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        DepartmentError.DEPARTMENT_NOT_FOUND
                                                                .getMessage())));
        departmentMapper.fillEntity(existDepartment, request);
        return departmentMapper.toDto(departmentRepository.save(existDepartment));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Department existDepartment =
                departmentRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new DepartmentNotFoundException(
                                                DepartmentError.DEPARTMENT_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        DepartmentError.DEPARTMENT_NOT_FOUND
                                                                .getMessage())));
        departmentRepository.delete(existDepartment);
    }
}
