package com.linhtn.authory_service.modules.partner.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.partner.domain.model.Partner;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface PartnerRepository {
    PagingResponse<Partner> getPage(PagingRequest pagingRequest);

    Optional<Partner> get(UUID id);

    Partner create(Partner partner);

    Partner update(Partner partner);

    void delete(UUID id);
}
