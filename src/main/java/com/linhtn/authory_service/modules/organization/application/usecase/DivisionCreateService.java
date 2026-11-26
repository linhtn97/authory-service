package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionCreateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.DivisionApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.DivisionCreateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Division;
import com.linhtn.authory_service.modules.organization.domain.repository.DivisionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DivisionCreateService implements DivisionCreateUseCase {
    private final DivisionRepository divisionRepository;

    public DivisionCreateService(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    @Override
    @Transactional
    public DivisionCreateResult execute(DivisionCreateCommand command) {
        log.info("Creating Division with code: {}", command.getCode());
        Division model = DivisionApplicationMapper.toDomain(command);
        Division created = divisionRepository.create(model);
        return DivisionApplicationMapper.toCreateResult(created);
    }
}
