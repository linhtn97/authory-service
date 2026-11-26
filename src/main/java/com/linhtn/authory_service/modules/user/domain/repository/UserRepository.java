package com.linhtn.authory_service.modules.user.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

public interface UserRepository {
    PagingResponse<User> getPage(PagingRequest pagingRequest);

    Optional<User> get(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

    User create(User user);

    User update(User user);

    void delete(UUID id);

    long count();

    long countNewUsersToday();
}
