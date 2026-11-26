package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.PositionCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionCreateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.PositionApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.PositionCreateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Position;
import com.linhtn.authory_service.modules.organization.domain.repository.PositionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PositionCreateService implements PositionCreateUseCase {
    private final PositionRepository positionRepository;

    public PositionCreateService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    @Transactional
    public PositionCreateResult execute(PositionCreateCommand command) {
        log.info("Creating Position with code: {}", command.getCode());
        Position model = PositionApplicationMapper.toDomain(command);
        Position created = positionRepository.create(model);
        return PositionApplicationMapper.toCreateResult(created);
    }
}
