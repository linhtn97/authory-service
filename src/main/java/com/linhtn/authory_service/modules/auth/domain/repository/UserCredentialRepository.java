package com.linhtn.authory_service.modules.auth.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.auth.domain.model.UserCredential;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface UserCredentialRepository {
    PagingResponse<UserCredential> getPage(PagingRequest pagingRequest);

    Optional<UserCredential> get(UUID id);

    UserCredential create(UserCredential userCredential);

    UserCredential update(UserCredential userCredential);

    void delete(UUID id);

    Optional<UserCredential> findByUserId(UUID userId);

    boolean existsByUserIdAndPassword(UUID userId, String password);
}
