package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.result.CenterGetResult;
import com.linhtn.authory_service.modules.organization.application.mapper.CenterApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.CenterGetUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Center;
import com.linhtn.authory_service.modules.organization.domain.repository.CenterRepository;
import com.linhtn.authory_service.modules.organization.exception.error.CenterError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.CenterNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CenterGetService implements CenterGetUseCase {
    private final CenterRepository centerRepository;
    private final MessageResolver i18nHelper;

    public CenterGetService(CenterRepository centerRepository, MessageResolver i18nHelper) {
        this.centerRepository = centerRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional(readOnly = true)
    public CenterGetResult execute(UUID id) {
        log.info("Fetching Center with ID: {}", id);
        Center model =
                centerRepository
                        .get(id)
                        .orElseThrow(
                                () ->
                                        new CenterNotFoundException(
                                                CenterError.CENTER_NOT_FOUND.name(),
                                                i18nHelper.getMessage(
                                                        CenterError.CENTER_NOT_FOUND.getMessage(),
                                                        id)));
        return CenterApplicationMapper.toGetResult(model);
    }
}
