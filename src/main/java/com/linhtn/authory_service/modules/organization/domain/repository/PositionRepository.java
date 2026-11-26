package com.linhtn.authory_service.modules.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.organization.domain.model.Position;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface PositionRepository {
    PagingResponse<Position> getPage(PagingRequest pagingRequest);

    Optional<Position> get(UUID id);

    Position create(Position position);

    Position update(Position position);

    void delete(UUID id);
}
