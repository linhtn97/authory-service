package com.linhtn.authory_service.modules.rbac.service.impl;

import com.linhtn.authory_service.modules.rbac.domain.dto.PermissionDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Permission;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionUpdateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.PermissionDeleteEvent;
import com.linhtn.authory_service.modules.rbac.domain.mapper.PermissionMapper;
import com.linhtn.authory_service.modules.rbac.exception.error.PermissionError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.PerrmisionNotFoundException;
import com.linhtn.authory_service.modules.rbac.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.service.PermissionService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;
import com.linhtn.authory_service.config.i18n.I18nHelper;
import org.springframework.context.ApplicationEventPublisher;
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
public class PermissionServiceImpl implements PermissionService {

    private final I18nHelper i18nHelper;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final ApplicationEventPublisher eventPublisher;

    public PermissionServiceImpl(I18nHelper i18nHelper, PermissionRepository permissionRepository, PermissionMapper permissionMapper, ApplicationEventPublisher eventPublisher) {
        this.i18nHelper = i18nHelper;
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PagingResponse<PermissionDto> getPage(PagingRequest request) {
        Specification<Permission> specification = new GenericSpecification<>(request);

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

        var paging = permissionRepository.findAll(specification, pageable);

        return permissionMapper.toPage(paging);
    }

    @Override
    public PermissionDto get(UUID id) {
        return permissionMapper.toDto(permissionRepository.findById(id).orElseThrow(() -> new PerrmisionNotFoundException(
                PermissionError.PERMISSION_NOT_FOUND.name(),
                i18nHelper.getMessage(PermissionError.PERMISSION_NOT_FOUND.getMessage())))
        );
    }

    @Override
    @Transactional
    public PermissionDto create(PermissionDto request) {
        Permission newPermission = new Permission();
        permissionMapper.fillEntity(newPermission, request);
        Permission savedPermission = permissionRepository.save(newPermission);
        eventPublisher.publishEvent(new PermissionCreateEvent(savedPermission));
        return permissionMapper.toDto(savedPermission);
    }

    @Override
    @Transactional
    public PermissionDto update(UUID id, PermissionDto request) {
        Permission existPermission = permissionRepository.findById(id).orElseThrow(() ->
                new PerrmisionNotFoundException(
                        PermissionError.PERMISSION_NOT_FOUND.name(),
                        i18nHelper.getMessage(PermissionError.PERMISSION_NOT_FOUND.getMessage()))
        );

        permissionMapper.fillEntity(existPermission, request);
        Permission savedPermission = permissionRepository.save(existPermission);
        eventPublisher.publishEvent(new PermissionUpdateEvent(savedPermission));
        return permissionMapper.toDto(savedPermission);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Permission existPermission = permissionRepository.findById(id).orElseThrow(() ->
                new PerrmisionNotFoundException(
                        PermissionError.PERMISSION_NOT_FOUND.name(),
                        i18nHelper.getMessage(PermissionError.PERMISSION_NOT_FOUND.getMessage()))
        );

        permissionRepository.delete(existPermission);
        eventPublisher.publishEvent(new PermissionDeleteEvent(existPermission));
    }
}
