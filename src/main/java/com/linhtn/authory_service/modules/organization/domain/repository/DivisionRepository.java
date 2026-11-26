package com.linhtn.authory_service.modules.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.organization.domain.model.Division;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface DivisionRepository {
    PagingResponse<Division> getPage(PagingRequest pagingRequest);

    Optional<Division> get(UUID id);

    Division create(Division division);

    Division update(Division division);

    void delete(UUID id);
}
