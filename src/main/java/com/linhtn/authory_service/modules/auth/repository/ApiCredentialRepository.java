package com.linhtn.authory_service.modules.auth.repository;

import com.linhtn.authory_service.modules.auth.domain.entity.ApiCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiCredentialRepository extends JpaRepository<ApiCredential, UUID>, JpaSpecificationExecutor<ApiCredential> {
    Optional<ApiCredential> findByClientIdAndHashedSecret(String clientId, String hashedSecret);
}
