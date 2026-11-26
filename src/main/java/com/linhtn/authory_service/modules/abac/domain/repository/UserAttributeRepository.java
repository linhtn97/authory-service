package com.linhtn.authory_service.modules.abac.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.abac.domain.model.UserAttribute;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface UserAttributeRepository {
    PagingResponse<UserAttribute> getPage(PagingRequest pagingRequest);

    Optional<UserAttribute> get(UUID id);

    UserAttribute create(UserAttribute userAttribute);

    UserAttribute update(UserAttribute userAttribute);

    void delete(UUID id);

    List<UserAttribute> findByUserId(UUID userId);
}
