package com.linhtn.authory_service.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.auth.domain.entity.ApiCredential;

@Repository
public interface ApiCredentialRepository
        extends JpaRepository<ApiCredential, UUID>, JpaSpecificationExecutor<ApiCredential> {
    Optional<ApiCredential> findByClientIdAndHashedSecret(String clientId, String hashedSecret);
}
