package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.port.in.DivisionDeleteUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.DivisionRepository;
import com.linhtn.authory_service.modules.organization.exception.error.DivisionError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.DivisionNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DivisionDeleteService implements DivisionDeleteUseCase {
    private final DivisionRepository divisionRepository;
    private final MessageResolver i18nHelper;

    public DivisionDeleteService(
            DivisionRepository divisionRepository, MessageResolver i18nHelper) {
        this.divisionRepository = divisionRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting Division with ID: {}", id);
        divisionRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new DivisionNotFoundException(
                                        DivisionError.DIVISION_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                DivisionError.DIVISION_NOT_FOUND.getMessage(),
                                                id)));
        divisionRepository.delete(id);
    }
}
