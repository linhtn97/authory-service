package com.linhtn.authory_service.modules.organization.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.port.in.BranchDeleteUseCase;
import com.linhtn.authory_service.modules.organization.domain.repository.BranchRepository;
import com.linhtn.authory_service.modules.organization.exception.error.BranchError;
import com.linhtn.authory_service.modules.organization.exception.unchecked.BranchNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BranchDeleteService implements BranchDeleteUseCase {
    private final BranchRepository branchRepository;
    private final MessageResolver i18nHelper;

    public BranchDeleteService(BranchRepository branchRepository, MessageResolver i18nHelper) {
        this.branchRepository = branchRepository;
        this.i18nHelper = i18nHelper;
    }

    @Override
    @Transactional
    public void execute(UUID id) {
        log.info("Deleting Branch with ID: {}", id);
        branchRepository
                .get(id)
                .orElseThrow(
                        () ->
                                new BranchNotFoundException(
                                        BranchError.BRANCH_NOT_FOUND.name(),
                                        i18nHelper.getMessage(
                                                BranchError.BRANCH_NOT_FOUND.getMessage(), id)));
        branchRepository.delete(id);
    }
}
