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
import com.linhtn.authory_service.modules.organization.domain.dto.DivisionDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Division;
import com.linhtn.authory_service.modules.organization.domain.mapper.DivisionMapper;
import com.linhtn.authory_service.modules.organization.exception.error.DivisionError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.DivisionNotFoundException;
import com.linhtn.authory_service.modules.organization.repository.DivisionRepository;
import com.linhtn.authory_service.modules.organization.service.DivisionService;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;

@Service
public class DivisionServiceImpl implements DivisionService {
    private final I18nHelper i18nHelper;
    private final DivisionRepository divisionRepository;
    private final DivisionMapper divisionMapper;

    public DivisionServiceImpl(
            I18nHelper i18nHelper,
            DivisionRepository divisionRepository,
            DivisionMapper divisionMapper) {
        this.i18nHelper = i18nHelper;
        this.divisionRepository = divisionRepository;
        this.divisionMapper = divisionMapper;
    }

    @Override
    public PagingResponse<DivisionDto> getPage(PagingRequest request) {
        Specification<Division> specification = new GenericSpecification<>(request);

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

        var paging = divisionRepository.findAll(specification, pageable);

        return divisionMapper.toPage(paging);
    }

    @Override
    public DivisionDto get(UUID id) {
        return divisionMapper.toDto(
                divisionRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new DivisionNotFoundException(
                                                DivisionError.DIVISION_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        DivisionError.DIVISION_NOT_FOUND
                                                                .getMessage()))));
    }

    @Override
    @Transactional
    public DivisionDto create(DivisionDto request) {
        var newDivision = new Division();
        divisionMapper.fillEntity(newDivision, request);
        return divisionMapper.toDto(divisionRepository.save(newDivision));
    }

    @Override
    @Transactional
    public DivisionDto update(UUID id, DivisionDto request) {
        Division existDivision =
                divisionRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage())));
        divisionMapper.fillEntity(existDivision, request);
        return divisionMapper.toDto(divisionRepository.save(existDivision));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Division existDivision =
                divisionRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage())));
        divisionRepository.delete(existDivision);
    }
}
