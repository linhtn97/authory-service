package com.linhtn.authory_service.modules.rbac.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.rbac.application.dto.result.GroupGetResult;
import com.linhtn.authory_service.modules.rbac.application.mapper.GroupApplicationMapper;
import com.linhtn.authory_service.modules.rbac.application.port.in.GroupGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.domain.repository.GroupRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GroupGetPageService implements GroupGetPageUseCase {
    private final GroupRepository groupRepository;

    public GroupGetPageService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<GroupGetResult> execute(PagingRequest pagingRequest) {
        log.info("Fetching paged Groups");
        var paged = groupRepository.getPage(pagingRequest);
        return paged.map(GroupApplicationMapper::toGetResult);
    }
}
