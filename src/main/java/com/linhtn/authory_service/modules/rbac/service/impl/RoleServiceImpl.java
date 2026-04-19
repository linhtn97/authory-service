package com.linhtn.authory_service.modules.rbac.service.impl;

import com.linhtn.authory_service.config.i18n.I18nHelper;
import com.linhtn.authory_service.modules.rbac.domain.dto.RoleDto;
import com.linhtn.authory_service.modules.rbac.domain.entity.Role;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleCreateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleUpdateEvent;
import com.linhtn.authory_service.modules.rbac.domain.event.RoleDeleteEvent;
import com.linhtn.authory_service.modules.rbac.domain.mapper.RoleMapper;
import com.linhtn.authory_service.modules.rbac.exception.error.RoleError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.RoleNotFoundException;
import com.linhtn.authory_service.modules.rbac.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.service.RoleService;
import com.linhtn.shared_kernel.common.model.FieldOrder;
import com.linhtn.shared_kernel.common.model.PagingRequest;
import com.linhtn.shared_kernel.common.model.PagingResponse;
import com.linhtn.shared_kernel.infrastructure.jpa.specification.GenericSpecification;
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
public class RoleServiceImpl implements RoleService {

    private final I18nHelper i18nHelper;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final ApplicationEventPublisher eventPublisher;

    public RoleServiceImpl(I18nHelper i18nHelper,
                           RoleRepository roleRepository,
                           RoleMapper roleMapper,
                           ApplicationEventPublisher eventPublisher) {
        this.i18nHelper = i18nHelper;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PagingResponse<RoleDto> getPage(PagingRequest request) {
        Specification<Role> specification = new GenericSpecification<>(request);

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

        var paging = roleRepository.findAll(specification, pageable);

        return roleMapper.toPage(paging);
    }

    @Override
    public RoleDto get(UUID id) {
        return roleMapper.toDto(
                roleRepository.findById(id).orElseThrow(() ->
                        new RoleNotFoundException(
                                RoleError.ROLE_NOT_FOUND.name(),
                                i18nHelper.getMessage(RoleError.ROLE_NOT_FOUND.getMessage())
                        )
                )
        );
    }

    @Override
    @Transactional
    public RoleDto create(RoleDto request) {
        Role newRole = new Role();
        roleMapper.fillEntity(newRole, request);
        Role savedRole = roleRepository.save(newRole);
        eventPublisher.publishEvent(new RoleCreateEvent(savedRole));
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public RoleDto update(UUID id, RoleDto request) {
        Role existRole = roleRepository.findById(id).orElseThrow(() ->
                new RoleNotFoundException(
                        RoleError.ROLE_NOT_FOUND.name(),
                        i18nHelper.getMessage(RoleError.ROLE_NOT_FOUND.getMessage())
                )
        );

        roleMapper.fillEntity(existRole, request);
        Role savedRole = roleRepository.save(existRole);
        eventPublisher.publishEvent(new RoleUpdateEvent(savedRole));
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Role existRole = roleRepository.findById(id).orElseThrow(() ->
                new RoleNotFoundException(
                        RoleError.ROLE_NOT_FOUND.name(),
                        i18nHelper.getMessage(RoleError.ROLE_NOT_FOUND.getMessage())
                )
        );

        roleRepository.delete(existRole);
        eventPublisher.publishEvent(new RoleDeleteEvent(existRole));
    }
}
