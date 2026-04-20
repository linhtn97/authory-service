package com.linhtn.authory_service.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.auth.domain.entity.UserCredential;

@Repository
public interface UserCredentialRepository
        extends JpaRepository<UserCredential, UUID>, JpaSpecificationExecutor<UserCredential> {
    Optional<UserCredential> findByUserId(UUID userId);

    boolean existsByUserIdAndPassword(UUID userId, String password);
}
