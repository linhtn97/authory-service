package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.PositionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.PositionUpdateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.PositionApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.PositionUpdateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Position;
import com.linhtn.authory_service.modules.organization.domain.repository.PositionRepository;
import com.linhtn.authory_service.modules.organization.exception.error.PositionError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.PositionNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PositionUpdateService implements PositionUpdateUseCase {
    private final PositionRepository positionRepository;
    private final MessageResolver i18nHelper;

    public PositionUpdateService(
            PositionRepository positionRepository, MessageResolver i18nHelper) {
        this.positionRepository = positionRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public PositionUpdateResult execute(UUID id, PositionUpdateCommand command) {
        log.info("Updating Position with ID: {}", id);
        Position existing =
                positionRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new PositionNotFoundException(
                                                PositionError.POSITION_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        PositionError.POSITION_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        PositionApplicationMapper.updateDomain(existing, command);
        Position updated = positionRepository.update(existing);
        return PositionApplicationMapper.toUpdateResult(updated);
    }
}
