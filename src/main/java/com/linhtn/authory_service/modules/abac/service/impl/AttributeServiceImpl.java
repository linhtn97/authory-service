package com.linhtn.authory_service.modules.abac.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.abac.domain.dto.AttributeDto;
import com.linhtn.authory_service.modules.abac.domain.entity.Attribute;
import com.linhtn.authory_service.modules.abac.domain.mapper.AttributeMapper;
import com.linhtn.authory_service.modules.abac.exception.error.AttributeError;
import com.linhtn.authory_service.modules.abac.exception.unchecked.AttributeNotFoundException;
import com.linhtn.authory_service.modules.abac.repository.AttributeRepository;
import com.linhtn.authory_service.modules.abac.service.AttributeService;
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
public class AttributeServiceImpl implements AttributeService {

    private final I18nHelper i18nHelper;
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    public AttributeServiceImpl(I18nHelper i18nHelper,
                                AttributeRepository attributeRepository,
                                AttributeMapper attributeMapper) {
        this.i18nHelper = i18nHelper;
        this.attributeRepository = attributeRepository;
        this.attributeMapper = attributeMapper;
    }

    @Override
    public PagingResponse<AttributeDto> getPage(PagingRequest request) {
        Specification<Attribute> specification = new GenericSpecification<>(request);

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

        var paging = attributeRepository.findAll(specification, pageable);

        return attributeMapper.toPage(paging);
    }

    @Override
    public AttributeDto get(UUID id) {
        return attributeMapper.toDto(attributeRepository.findById(id).orElseThrow(() ->
                new AttributeNotFoundException(
                        AttributeError.ATTRIBUTE_NOT_FOUND.name(),
                        i18nHelper.getMessage(AttributeError.ATTRIBUTE_NOT_FOUND.getMessage())
                )
        ));
    }

    @Override
    @Transactional
    public AttributeDto create(AttributeDto request) {
        Attribute newAttribute = new Attribute();
        attributeMapper.fillEntity(newAttribute, request);
        return attributeMapper.toDto(attributeRepository.save(newAttribute));
    }

    @Override
    @Transactional
    public AttributeDto update(UUID id, AttributeDto request) {
        Attribute existAttribute = attributeRepository.findById(id).orElseThrow(() ->
                new AttributeNotFoundException(
                        AttributeError.ATTRIBUTE_NOT_FOUND.name(),
                        i18nHelper.getMessage(AttributeError.ATTRIBUTE_NOT_FOUND.getMessage())
                )
        );

        attributeMapper.fillEntity(existAttribute, request);
        return attributeMapper.toDto(attributeRepository.save(existAttribute));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attribute existAttribute = attributeRepository.findById(id).orElseThrow(() ->
                new AttributeNotFoundException(
                        AttributeError.ATTRIBUTE_NOT_FOUND.name(),
                        i18nHelper.getMessage(AttributeError.ATTRIBUTE_NOT_FOUND.getMessage())
                )
        );

        attributeRepository.delete(existAttribute);
    }
}

