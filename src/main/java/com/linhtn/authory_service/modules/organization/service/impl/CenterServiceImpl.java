package com.linhtn.authory_service.modules.organization.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.organization.domain.dto.CenterDto;
import com.linhtn.authory_service.modules.organization.domain.entity.Center;
import com.linhtn.authory_service.modules.organization.domain.mapper.CenterMapper;
import com.linhtn.authory_service.modules.organization.exception.error.CenterError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CenterNotFoundException;
import com.linhtn.authory_service.modules.organization.repository.CenterRepository;
import com.linhtn.authory_service.modules.organization.service.CenterService;
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
public class CenterServiceImpl implements CenterService {
    private final I18nHelper i18nHelper;
    private final CenterRepository centerRepository;
    private final CenterMapper centerMapper;

    public CenterServiceImpl(I18nHelper i18nHelper, CenterRepository centerRepository, CenterMapper centerMapper) {
        this.i18nHelper = i18nHelper;
        this.centerRepository = centerRepository;
        this.centerMapper = centerMapper;
    }

    @Override
    public PagingResponse<CenterDto> getPage(PagingRequest request) {
        Specification<Center> specification = new GenericSpecification<>(request);

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

        var paging = centerRepository.findAll(specification, pageable);

        return centerMapper.toPage(paging);
    }

    @Override
    public CenterDto get(UUID id) {
        return centerMapper.toDto(centerRepository.findById(id).orElseThrow(() -> new CenterNotFoundException(
                CenterError.CENTER_NOT_FOUND.name(),
                i18nHelper.getMessage(CenterError.CENTER_NOT_FOUND.getMessage())))
        );
    }

    @Override
    @Transactional
    public CenterDto create(CenterDto request) {
        var newCenter = new Center();
        centerMapper.fillEntity(newCenter, request);
        return centerMapper.toDto(centerRepository.save(newCenter));
    }

    @Override
    @Transactional
    public CenterDto update(UUID id, CenterDto request) {
        Center existCenter = centerRepository.findById(id).orElseThrow(() -> new CenterNotFoundException(
                CenterError.CENTER_NOT_FOUND.name(),
                i18nHelper.getMessage(CenterError.CENTER_NOT_FOUND.getMessage()))
        );
        centerMapper.fillEntity(existCenter, request);
        return centerMapper.toDto(centerRepository.save(existCenter));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Center existCenter = centerRepository.findById(id).orElseThrow(() -> new CenterNotFoundException(
                CenterError.CENTER_NOT_FOUND.name(),
                i18nHelper.getMessage(CenterError.CENTER_NOT_FOUND.getMessage()))
        );
        centerRepository.delete(existCenter);
    }
}
