package com.linhtn.authory_service.modules.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.organization.domain.model.Branch;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface BranchRepository {
    PagingResponse<Branch> getPage(PagingRequest pagingRequest);

    Optional<Branch> get(UUID id);

    Branch create(Branch branch);

    Branch update(Branch branch);

    void delete(UUID id);
}
