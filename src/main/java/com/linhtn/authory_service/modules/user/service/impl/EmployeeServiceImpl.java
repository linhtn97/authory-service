package com.linhtn.authory_service.modules.user.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.user.domain.dto.EmployeeDto;
import com.linhtn.authory_service.modules.user.domain.entity.Employee;
import com.linhtn.authory_service.modules.user.domain.mapper.EmployeeMapper;
import com.linhtn.authory_service.modules.user.exception.error.EmployeeError;
import com.linhtn.authory_service.modules.user.exception.unchecked.EmployeeNotFoundException;
import com.linhtn.authory_service.modules.user.repository.EmployeeRepository;
import com.linhtn.authory_service.modules.user.service.EmployeeService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final I18nHelper i18nHelper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(I18nHelper i18nHelper,
                               EmployeeRepository employeeRepository,
                               EmployeeMapper employeeMapper) {
        this.i18nHelper = i18nHelper;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public PagingResponse<EmployeeDto> getPage(PagingRequest request) {
        Specification<Employee> specification = new GenericSpecification<>(request);

        List<Sort.Order> orders = new ArrayList<>();
        request.getOrders().forEach(o -> {
            if (o.getSort().equals(FieldOrder.SortType.asc)) {
                orders.add(Sort.Order.asc(o.getField()));
            } else if (o.getSort().equals(FieldOrder.SortType.desc)) {
                orders.add(Sort.Order.desc(o.getField()));
            }
        });

        Sort sort = Sort.by(orders);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sort);

        var paging = employeeRepository.findAll(specification, pageable);

        return employeeMapper.toPage(paging);
    }

    @Override
    public EmployeeDto get(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        EmployeeError.EMPLOYEE_NOT_FOUND.name(),
                        i18nHelper.getMessage(EmployeeError.EMPLOYEE_NOT_FOUND.getMessage(), id)
                ));
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public EmployeeDto create(EmployeeDto request) {
        Employee employee = new Employee();
        employeeMapper.fillEntity(employee, request);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeDto update(UUID id, EmployeeDto request) {
        Employee existEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        EmployeeError.EMPLOYEE_NOT_FOUND.name(),
                        i18nHelper.getMessage(EmployeeError.EMPLOYEE_NOT_FOUND.getMessage())
                ));

        employeeMapper.fillEntity(existEmployee, request);
        return employeeMapper.toDto(employeeRepository.save(existEmployee));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Employee existEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        EmployeeError.EMPLOYEE_NOT_FOUND.name(),
                        i18nHelper.getMessage(EmployeeError.EMPLOYEE_NOT_FOUND.getMessage())
                ));
        employeeRepository.delete(existEmployee);
    }
}

