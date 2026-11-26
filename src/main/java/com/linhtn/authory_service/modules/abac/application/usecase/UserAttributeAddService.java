package com.linhtn.authory_service.modules.abac.application.usecase;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.command.UserAttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.mapper.UserAttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeAddUseCase;
import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserAttributeAddService implements UserAttributeAddUseCase {
    private final UserAttributeRepository userAttributeRepository;

    public UserAttributeAddService(UserAttributeRepository userAttributeRepository) {
        this.userAttributeRepository = userAttributeRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user-attributes", allEntries = true)
    public void execute(UUID userId, UserAttributeCreateCommand command) {
        log.info("Adding attribute to user: {}", userId);
        UserAttribute model = UserAttributeApplicationMapper.toDomain(command);
        model.setUserId(userId);
        userAttributeRepository.create(model);
    }
}
