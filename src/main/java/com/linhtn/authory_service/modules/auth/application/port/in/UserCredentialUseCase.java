package com.linhtn.authory_service.modules.auth.application.port.in;

import java.util.UUID;

import com.linhtn.authory_service.modules.auth.application.dto.result.UserCredentialResult;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface UserCredentialUseCase {
    PagingResponse<UserCredentialResult> getPage(PagingRequest request);

    UserCredentialResult get(UUID id);

    UserCredentialResult create(UserCredentialResult request);

    UserCredentialResult update(UUID id, UserCredentialResult request);

    void delete(UUID id);

    UserCredentialResult getByUserId(UUID userId);

    boolean existsByUserIdAndPassword(UUID userId, String password);

    UserCredentialResult validateAndTrackLogin(UUID userId, String password);

    void unlockAccount(UUID userId);

    void updateMfa(UUID userId, boolean enabled, String secret);

    void resetPassword(UUID userId, String newEncodedPassword);
}
