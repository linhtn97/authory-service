package com.linhtn.authory_service.modules.rbac.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.linhtn.authory_service.modules.rbac.application.dto.result.RoleGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.RoleApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetUseCase;
import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.authory_service.modules.rbac.domain.repository.RoleRepository;
import com.linhtn.authory_service.modules.rbac.exception.error.RoleError;
import com.linhtn.authory_service.modules.rbac.exception.unchecked.RoleNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@Service
public class RoleGetService implements RoleGetUseCase {

    private final RoleRepository roleRepository;
    private final MessageResolver i18nHelper;

    public RoleGetService(RoleRepository roleRepository, MessageResolver i18nHelper) {
        this.roleRepository = roleRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    public RoleGetResult execute(UUID id) {
        Role entity =
                roleRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new RoleNotFoundException(
                                                RoleError.ROLE_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        RoleError.ROLE_NOT_FOUND.getMessage(),
                                                        id)));
        return RoleApplicationMapper.toGetResult(entity);
    }

    @Override
    public long count() {
        return roleRepository.count();
    }
}
