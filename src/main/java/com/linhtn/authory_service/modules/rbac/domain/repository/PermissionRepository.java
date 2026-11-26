package com.linhtn.authory_service.modules.rbac.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.rbac.domain.model.Permission;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface PermissionRepository {
    PagingResponse<Permission> getPage(PagingRequest pagingRequest);

    Optional<Permission> get(UUID id);

    Permission create(Permission permission);

    Permission update(Permission permission);

    void delete(UUID id);

    boolean existsById(UUID id);

    List<Permission> findAllById(Iterable<UUID> ids);

    long count();
}
