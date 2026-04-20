package com.linhtn.authory_service.modules.rbac.service.impl;

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
import com.linhtn.authory_service.modules.rbac.domain.dto.ResourceDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Resource;
import com.linhtn.authory_service.modules.rbac.domain.mapper.ResourceMapper;
import com.linhtn.authory_service.modules.rbac.exception.error.ResourceError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.ResourceNotFoundException;
import com.linhtn.authory_service.modules.rbac.repository.ResourceRepository;
import com.linhtn.authory_service.modules.rbac.service.ResourceService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final I18nHelper i18nHelper;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public ResourceServiceImpl(
            I18nHelper i18nHelper,
            ResourceRepository resourceRepository,
            ResourceMapper resourceMapper) {
        this.i18nHelper = i18nHelper;
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public PagingResponse<ResourceDto> getPage(PagingRequest request) {
        Specification<Resource> specification = new GenericSpecification<>(request);

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

        var paging = resourceRepository.findAll(specification, pageable);

        return resourceMapper.toPage(paging);
    }

    @Override
    public ResourceDto get(UUID id) {
        return resourceMapper.toDto(
                resourceRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ResourceError.RESOURCE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ResourceError.RESOURCE_NOT_FOUND
                                                                .getMessage()))));
    }

    @Override
    @Transactional
    public ResourceDto create(ResourceDto request) {
        Resource newResource = new Resource();
        resourceMapper.fillEntity(newResource, request);
        return resourceMapper.toDto(resourceRepository.save(newResource));
    }

    @Override
    @Transactional
    public ResourceDto update(UUID id, ResourceDto request) {
        Resource existResource =
                resourceRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ResourceError.RESOURCE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ResourceError.RESOURCE_NOT_FOUND
                                                                .getMessage())));

        resourceMapper.fillEntity(existResource, request);
        return resourceMapper.toDto(resourceRepository.save(existResource));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Resource existResource =
                resourceRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ResourceError.RESOURCE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        ResourceError.RESOURCE_NOT_FOUND
                                                                .getMessage())));

        resourceRepository.delete(existResource);
    }
}
