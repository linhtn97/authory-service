package com.linhtn.authory_service.modules.organization.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.organization.domain.model.Department;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface DepartmentRepository {
    PagingResponse<Department> getPage(PagingRequest pagingRequest);

    Optional<Department> get(UUID id);

    Department create(Department department);

    Department update(Department department);

    void delete(UUID id);
}
