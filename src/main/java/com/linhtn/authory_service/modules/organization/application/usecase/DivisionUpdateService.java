package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.DivisionUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.DivisionUpdateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.DivisionApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.DivisionUpdateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Division;
import com.linhtn.authory_service.modules.organization.domain.repository.DivisionRepository;
import com.linhtn.authory_service.modules.organization.exception.error.DivisionError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.DivisionNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DivisionUpdateService implements DivisionUpdateUseCase {
    private final DivisionRepository divisionRepository;
    private final MessageResolver i18nHelper;

    public DivisionUpdateService(
            DivisionRepository divisionRepository, MessageResolver i18nHelper) {
        this.divisionRepository = divisionRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public DivisionUpdateResult execute(UUID id, DivisionUpdateCommand command) {
        log.info("Updating Division with ID: {}", id);
        Division existing =
                divisionRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new DivisionNotFoundException(
                                                DivisionError.DIVISION_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        DivisionError.DIVISION_NOT_FOUND
                                                                .getMessage(),
                                                        id)));
        DivisionApplicationMapper.updateDomain(existing, command);
        Division updated = divisionRepository.update(existing);
        return DivisionApplicationMapper.toUpdateResult(updated);
    }
}
