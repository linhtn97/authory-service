package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentDeleteUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.DepartmentRepository;
import com.linhtn.authory_service.modules.organization.exception.error.DepartmentError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.DepartmentNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartmentDeleteService implements DepartmentDeleteUseCase {
    private final DepartmentRepository departmentRepository;
    private final MessageResolver i18nHelper;

    public DepartmentDeleteService(
            DepartmentRepository departmentRepository, MessageResolver i18nHelper) {
        this.departmentRepository = departmentRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting Department with ID: {}", id);
        departmentRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new DepartmentNotFoundException(
                                        DepartmentError.DEPARTMENT_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                DepartmentError.DEPARTMENT_NOT_FOUND.getMessage(),
                                                id)));
        departmentRepository.delete(id);
    }
}
