package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.CenterCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterCreateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CenterApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterCreateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Center;
import com.linhtn.authory_service.modules.organization.domain.repository.CenterRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CenterCreateService implements CenterCreateUseCase {
    private final CenterRepository centerRepository;

    public CenterCreateService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    @Override
    @Transactional
    public CenterCreateResult execute(CenterCreateCommand command) {
        log.info("Creating Center with code: {}", command.getCode());
        Center model = CenterApplicationMapper.toDomain(command);
        Center created = centerRepository.create(model);
        return CenterApplicationMapper.toCreateResult(created);
    }
}
