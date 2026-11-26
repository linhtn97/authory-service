package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.CenterUpdateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.CenterUpdateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CenterApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterUpdateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Center;
import com.linhtn.authory_service.modules.organization.domain.repository.CenterRepository;
import com.linhtn.authory_service.modules.organization.exception.error.CenterError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CenterNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CenterUpdateService implements CenterUpdateUseCase {
    private final CenterRepository centerRepository;
    private final MessageResolver i18nHelper;

    public CenterUpdateService(CenterRepository centerRepository, MessageResolver i18nHelper) {
        this.centerRepository = centerRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public CenterUpdateResult execute(UUID id, CenterUpdateCommand command) {
        log.info("Updating Center with ID: {}", id);
        Center existing =
                centerRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new CenterNotFoundException(
                                                CenterError.CENTER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        CenterError.CENTER_NOT_FOUND.getMessage(),
                                                        id)));
        CenterApplicationMapper.updateDomain(existing, command);
        Center updated = centerRepository.update(existing);
        return CenterApplicationMapper.toUpdateResult(updated);
    }
}
