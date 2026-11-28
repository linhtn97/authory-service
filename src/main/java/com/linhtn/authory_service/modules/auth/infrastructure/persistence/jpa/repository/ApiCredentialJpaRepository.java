package com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.ApiCredentialEntity;

@Repository
public interface ApiCredentialJpaRepository
        extends JpaRepository<ApiCredentialEntity, UUID>,
                JpaSpecificationExecutor<ApiCredentialEntity> {

    Optional<ApiCredentialEntity> findByClientIdAndHashedSecret(
            String clientId, String hashedSecret);

    Optional<ApiCredentialEntity> findByApiKey(String apiKey);

    Optional<ApiCredentialEntity> findByUserId(UUID userId);

    List<ApiCredentialEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<ApiCredentialEntity> findByIdAndUserId(UUID id, UUID userId);
}
