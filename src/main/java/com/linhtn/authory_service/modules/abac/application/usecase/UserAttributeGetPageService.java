package com.linhtn.authory_service.modules.abac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.abac.application.dto.result.UserAttributeGetResult;
import com.linhtn.authory_service.modules.abac.application.mapper.UserAttributeApplicationMapper;
import com.linhtn.authory_service.modules.abac.application.port.in.UserAttributeGetPageUseCase;
import com.linhtn.authory_service.modules.abac.domain.repository.UserAttributeRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserAttributeGetPageService implements UserAttributeGetPageUseCase {
    private final UserAttributeRepository userAttributeRepository;

    public UserAttributeGetPageService(UserAttributeRepository userAttributeRepository) {
        this.userAttributeRepository = userAttributeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<UserAttributeGetResult> execute(PagingRequest pagingRequest) {
        log.info("Fetching paged user attributes");
        var paged = userAttributeRepository.getPage(pagingRequest);
        return paged.map(UserAttributeApplicationMapper::toGetResult);
    }
}
