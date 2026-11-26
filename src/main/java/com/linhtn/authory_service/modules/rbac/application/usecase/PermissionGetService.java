package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.PermissionGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.PermissionApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.PermissionGetUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.authory_service.modules.rbac.domain.repository.PermissionRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.PermissionError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.PermissionNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionGetService implements PermissionGetUseCase {
    private final PermissionRepository permissionRepository;
    private final MessageResolver i18nHelper;

    public PermissionGetService(
            PermissionRepository permissionRepository, MessageResolver i18nHelper) {
        this.permissionRepository = permissionRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionGetResult execute(UUID id) {
        log.info("Fetching Permission with ID: {}", id);
        Permission model =
                permissionRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new PermissionNotFoundException(
                                                PermissionError.PERMISSION_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        PermissionError.PERMISSION_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        return PermissionApplicationMapper.toGetResult(model);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return permissionRepository.count();
    }
}
