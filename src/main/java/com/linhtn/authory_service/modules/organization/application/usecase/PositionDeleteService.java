package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.port.in.PositionDeleteUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.PositionRepository;
import com.linhtn.authory_service.modules.organization.exception.error.PositionError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.PositionNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PositionDeleteService implements PositionDeleteUseCase {
    private final PositionRepository positionRepository;
    private final MessageResolver i18nHelper;

    public PositionDeleteService(
            PositionRepository positionRepository, MessageResolver i18nHelper) {
        this.positionRepository = positionRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting Position with ID: {}", id);
        positionRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new PositionNotFoundException(
                                        PositionError.POSITION_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                PositionError.POSITION_NOT_FOUND.getMessage(),
                                                id)));
        positionRepository.delete(id);
    }
}
