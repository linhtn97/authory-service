package com.linhtn.authory_service.modules.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.organization.domain.model.Center;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface CenterRepository {
    PagingResponse<Center> getPage(PagingRequest pagingRequest);

    Optional<Center> get(UUID id);

    Center create(Center center);

    Center update(Center center);

    void delete(UUID id);
}
