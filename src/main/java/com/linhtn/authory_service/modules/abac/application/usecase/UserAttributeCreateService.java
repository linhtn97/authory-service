package com.linhtn.authory_service.modules.abac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeCreateResult;
import com.linhtn.authory_service.modules.abac.application.mapper.UserAttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeCreateUseCase;
import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserAttributeCreateService implements UserAttributeCreateUseCase {
    private final UserAttributeRepository userAttributeRepository;

    public UserAttributeCreateService(UserAttributeRepository userAttributeRepository) {
        this.userAttributeRepository = userAttributeRepository;
    }

    @Override
    @Transactional
    public UserAttributeCreateResult execute(UserAttributeCreateCommand command) {
        log.info("Creating user attribute with key: {}", command.getKey());
        UserAttribute model = UserAttributeApplicationMapper.toDomain(command);
        return UserAttributeApplicationMapper.toCreateResult(userAttributeRepository.create(model));
    }
}
