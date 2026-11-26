package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.Role;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface RoleRepository {
    PagingResponse<Role> getPage(PagingRequest pagingRequest);

    Optional<Role> get(UUID id);

    Role create(Role role);

    Role update(Role role);

    void delete(UUID id);

    boolean existsById(UUID id);

    List<Role> findAllById(Iterable<UUID> ids);

    long count();
}
