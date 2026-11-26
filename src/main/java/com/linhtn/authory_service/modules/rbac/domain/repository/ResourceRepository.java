package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.Resource;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface ResourceRepository {
    PagingResponse<Resource> getPage(PagingRequest pagingRequest);

    Optional<Resource> get(UUID id);

    Resource create(Resource resource);

    Resource update(Resource resource);

    void delete(UUID id);

    boolean existsById(UUID id);

    List<Resource> findAllById(Iterable<UUID> ids);

    long count();
}
