package com.linhtn.authory_service.modules.organization.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.organization.application.dto.command.BranchCreateCommand;
import com.linhtn.authory_service.modules.organization.application.dto.result.BranchCreateResult;
import com.linhtn.authory_service.modules.organization.application.mapper.BranchApplicationMapper;
import com.linhtn.authory_service.modules.organization.application.port.in.BranchCreateUseCase;
import com.linhtn.authory_service.modules.organization.domain.model.Branch;
import com.linhtn.authory_service.modules.organization.domain.repository.BranchRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BranchCreateService implements BranchCreateUseCase {
    private final BranchRepository branchRepository;

    public BranchCreateService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public BranchCreateResult execute(BranchCreateCommand command) {
        log.info("Creating Branch with code: {}", command.getCode());
        Branch model = BranchApplicationMapper.toDomain(command);
        Branch created = branchRepository.create(model);
        return BranchApplicationMapper.toCreateResult(created);
    }
}
