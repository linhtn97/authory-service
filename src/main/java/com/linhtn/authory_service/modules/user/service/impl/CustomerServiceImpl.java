package com.linhtn.authory_service.modules.user.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.user.domain.dto.CustomerDto;
import com.linhtn.authory_service.modules.user.domain.entity.Customer;
import com.linhtn.authory_service.modules.user.domain.mapper.CustomerMapper;
import com.linhtn.authory_service.modules.user.exception.error.CustomerError;
import com.linhtn.authory_service.modules.user.exception.unchecked.CustomerNotFoundException;
import com.linhtn.authory_service.modules.user.repository.CustomerRepository;
import com.linhtn.authory_service.modules.user.service.CustomerService;
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
public class CustomerServiceImpl implements CustomerService {

    private final I18nHelper i18nHelper;
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(I18nHelper i18nHelper,
                               CustomerRepository customerRepository,
                               CustomerMapper customerMapper) {
        this.i18nHelper = i18nHelper;
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public PagingResponse<CustomerDto> getPage(PagingRequest request) {
        Specification<Customer> specification = new GenericSpecification<>(request);

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

        var paging = customerRepository.findAll(specification, pageable);

        return customerMapper.toPage(paging);
    }

    @Override
    public CustomerDto get(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CustomerError.CUSTOMER_NOT_FOUND.name(),
                        i18nHelper.getMessage(CustomerError.CUSTOMER_NOT_FOUND.getMessage(), id)
                ));
        return customerMapper.toDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto create(CustomerDto request) {
        var newCustomer = new Customer();
        customerMapper.fillEntity(newCustomer, request);
        return customerMapper.toDto(customerRepository.save(newCustomer));
    }

    @Override
    @Transactional
    public CustomerDto update(UUID id, CustomerDto request) {
        Customer existCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CustomerError.CUSTOMER_NOT_FOUND.name(),
                        i18nHelper.getMessage(CustomerError.CUSTOMER_NOT_FOUND.getMessage())
                ));

        customerMapper.fillEntity(existCustomer, request);
        return customerMapper.toDto(customerRepository.save(existCustomer));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Customer existCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        CustomerError.CUSTOMER_NOT_FOUND.name(),
                        i18nHelper.getMessage(CustomerError.CUSTOMER_NOT_FOUND.getMessage())
                ));
        customerRepository.delete(existCustomer);
    }
}
