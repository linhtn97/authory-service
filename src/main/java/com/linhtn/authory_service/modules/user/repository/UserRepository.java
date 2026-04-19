package com.linhtn.authory_service.modules.user.repository;

import com.linhtn.authory_service.modules.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query(value = "SELECT COUNT(*) FROM auth.users WHERE created_at >= CURRENT_DATE", nativeQuery = true)
    long countNewUsersToday();
}
