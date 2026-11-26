package com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.UserCredentialEntity;

@Repository
public interface UserCredentialJpaRepository
        extends JpaRepository<UserCredentialEntity, UUID>,
                JpaSpecificationExecutor<UserCredentialEntity> {

    Optional<UserCredentialEntity> findByUserId(UUID userId);

    boolean existsByUserIdAndPassword(UUID userId, String password);
}
