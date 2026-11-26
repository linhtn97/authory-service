package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.port.in.CenterDeleteUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.CenterRepository;
import com.linhtn.authory_service.modules.organization.exception.error.CenterError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CenterNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CenterDeleteService implements CenterDeleteUseCase {
    private final CenterRepository centerRepository;
    private final MessageResolver i18nHelper;

    public CenterDeleteService(CenterRepository centerRepository, MessageResolver i18nHelper) {
        this.centerRepository = centerRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting Center with ID: {}", id);
        centerRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new CenterNotFoundException(
                                        CenterError.CENTER_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                CenterError.CENTER_NOT_FOUND.getMessage(), id)));
        centerRepository.delete(id);
    }
}
