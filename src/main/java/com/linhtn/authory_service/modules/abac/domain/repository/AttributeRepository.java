package com.linhtn.authory_service.modules.abac.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface AttributeRepository {
    PagingResponse<Attribute> getPage(PagingRequest pagingRequest);

    Optional<Attribute> get(UUID id);

    Attribute create(Attribute attribute);

    Attribute update(Attribute attribute);

    void delete(UUID id);
}
