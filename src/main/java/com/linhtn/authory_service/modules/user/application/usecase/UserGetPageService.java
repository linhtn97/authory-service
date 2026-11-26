package com.linhtn.authory_service.modules.user.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.mapper.UserApplicationMapper;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetPageUseCase;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserGetPageService implements UserGetPageUseCase {

    private final UserRepository userRepository;

    public UserGetPageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<UserGetResult> execute(PagingRequest pagingRequest) {
        log.info(
                "Fetching paged users (page: {}, size: {})",
                pagingRequest.getPage(),
                pagingRequest.getPageSize());
        var paged = userRepository.getPage(pagingRequest);
        return paged.map(UserApplicationMapper::toGetResult);
    }
}
