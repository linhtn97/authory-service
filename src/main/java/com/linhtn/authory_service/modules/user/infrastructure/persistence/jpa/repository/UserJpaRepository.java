package com.linhtn.authory_service.modules.user.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.user.infrastructure.persistence.jpa.entity.UserEntity;

@Repository
public interface UserJpaRepository
        extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query(
            value = "SELECT COUNT(*) FROM auth.users WHERE created_at >= CURRENT_DATE",
            nativeQuery = true)
    long countNewUsersToday();
}
