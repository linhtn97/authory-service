package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.Group;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface GroupRepository {
    PagingResponse<Group> getPage(PagingRequest pagingRequest);

    Optional<Group> get(UUID id);

    Group create(Group group);

    Group update(Group group);

    void delete(UUID id);

    boolean existsById(UUID id);

    List<Group> findAllById(Iterable<UUID> ids);

    long count();
}
