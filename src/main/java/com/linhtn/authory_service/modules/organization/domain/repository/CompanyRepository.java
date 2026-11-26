package com.linhtn.authory_service.modules.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.organization.domain.model.Company;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface CompanyRepository {
    PagingResponse<Company> getPage(PagingRequest pagingRequest);

    Optional<Company> get(UUID id);

    Company create(Company company);

    Company update(Company company);

    void delete(UUID id);
}
