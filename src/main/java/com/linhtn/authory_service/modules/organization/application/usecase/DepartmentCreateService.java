package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.DepartmentCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DepartmentCreateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.DepartmentApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.DepartmentCreateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Department;
import com.linhtn.authory_service.modules.organization.domain.repository.DepartmentRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartmentCreateService implements DepartmentCreateUseCase {
    private final DepartmentRepository departmentRepository;

    public DepartmentCreateService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public DepartmentCreateResult execute(DepartmentCreateCommand command) {
        log.info("Creating Department with code: {}", command.getCode());
        Department model = DepartmentApplicationMapper.toDomain(command);
        Department created = departmentRepository.create(model);
        return DepartmentApplicationMapper.toCreateResult(created);
    }
}
