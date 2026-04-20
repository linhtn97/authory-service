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
import com.linhtn.authory_service.modules.organization.domain.dto.CompanyDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Company;
import com.linhtn.authory_service.modules.organization.domain.mapper.CompanyMapper;
import com.linhtn.authory_service.modules.organization.exception.error.CompanyError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CompanyNotFoundException;
import com.linhtn.authory_service.modules.organization.repository.CompanyRepository;
import com.linhtn.authory_service.modules.organization.service.CompanyService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final I18nHelper i18nHelper;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(
            I18nHelper i18nHelper,
            CompanyRepository companyRepository,
            CompanyMapper companyMapper) {
        this.i18nHelper = i18nHelper;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Override
    public PagingResponse<CompanyDto> getPage(PagingRequest request) {
        Specification<Company> specification = new GenericSpecification<>(request);

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

        var paging = companyRepository.findAll(specification, pageable);

        return companyMapper.toPage(paging);
    }

    @Override
    public CompanyDto get(UUID id) {
        return companyMapper.toDto(
                companyRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new CompanyNotFoundException(
                                                CompanyError.COMPANY_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        CompanyError.COMPANY_NOT_FOUND
                                                                .getMessage()))));
    }

    @Override
    @Transactional
    public CompanyDto create(CompanyDto request) {
        var newCompany = new Company();
        companyMapper.fillEntity(newCompany, request);
        return companyMapper.toDto(companyRepository.save(newCompany));
    }

    @Override
    @Transactional
    public CompanyDto update(UUID id, CompanyDto request) {
        Company existCompany =
                companyRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new CompanyNotFoundException(
                                                CompanyError.COMPANY_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        CompanyError.COMPANY_NOT_FOUND
                                                                .getMessage())));
        companyMapper.fillEntity(existCompany, request);
        return companyMapper.toDto(companyRepository.save(existCompany));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Company existCompany =
                companyRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new CompanyNotFoundException(
                                                CompanyError.COMPANY_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        CompanyError.COMPANY_NOT_FOUND
                                                                .getMessage())));
        companyRepository.delete(existCompany);
    }
}
