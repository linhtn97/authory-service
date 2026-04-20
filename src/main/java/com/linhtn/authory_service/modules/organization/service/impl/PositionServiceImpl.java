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
import com.linhtn.authory_service.modules.organization.domain.dto.PositionDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Position;
import com.linhtn.authory_service.modules.organization.domain.mapper.PositionMapper;
import com.linhtn.authory_service.modules.organization.exception.error.PositionError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.PositionNotFoundException;
import com.linhtn.authory_service.modules.organization.repository.PositionRepository;
import com.linhtn.authory_service.modules.organization.service.PositionService;
import com.linhtn.authory_service.modules.user.exception.error.UserError;
import com.linhtn.authory_service.modules.user.exception.unchecked.UserNotFoundException;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;

@Service
public class PositionServiceImpl implements PositionService {
    private final I18nHelper i18nHelper;
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public PositionServiceImpl(
            I18nHelper i18nHelper,
            PositionRepository positionRepository,
            PositionMapper positionMapper) {
        this.i18nHelper = i18nHelper;
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    @Override
    public PagingResponse<PositionDto> getPage(PagingRequest request) {
        Specification<Position> specification = new GenericSpecification<>(request);

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

        var paging = positionRepository.findAll(specification, pageable);

        return positionMapper.toPage(paging);
    }

    @Override
    public PositionDto get(UUID id) {
        return positionMapper.toDto(
                positionRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new PositionNotFoundException(
                                                PositionError.POSITION_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        PositionError.POSITION_NOT_FOUND
                                                                .getMessage()))));
    }

    @Override
    @Transactional
    public PositionDto create(PositionDto request) {
        var newPosition = new Position();
        positionMapper.fillEntity(newPosition, request);
        return positionMapper.toDto(positionRepository.save(newPosition));
    }

    @Override
    @Transactional
    public PositionDto update(UUID id, PositionDto request) {
        Position existPosition =
                positionRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage())));
        positionMapper.fillEntity(existPosition, request);
        return positionMapper.toDto(positionRepository.save(existPosition));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Position existPosition =
                positionRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                UserError.USER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        UserError.USER_NOT_FOUND.getMessage())));
        positionRepository.delete(existPosition);
    }
}
