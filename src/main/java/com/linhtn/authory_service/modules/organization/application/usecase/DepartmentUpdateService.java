package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentUpdateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.DepartmentApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentUpdateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Department;
import com.linhtn.authory_service.modules.organization.domain.repository.DepartmentRepository;
import com.linhtn.authory_service.modules.organization.exception.error.DepartmentError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.DepartmentNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartmentUpdateService implements DepartmentUpdateUseCase {
    private final DepartmentRepository departmentRepository;
    private final MessageResolver i18nHelper;

    public DepartmentUpdateService(
            DepartmentRepository departmentRepository, MessageResolver i18nHelper) {
        this.departmentRepository = departmentRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public DepartmentUpdateResult execute(UUID id, DepartmentUpdateCommand command) {
        log.info("Updating Department with ID: {}", id);
        Department existing =
                departmentRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new DepartmentNotFoundException(
                                                DepartmentError.DEPARTMENT_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        DepartmentError.DEPARTMENT_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        DepartmentApplicationMapper.updateDomain(existing, command);
        Department updated = departmentRepository.update(existing);
        return DepartmentApplicationMapper.toUpdateResult(updated);
    }
}
