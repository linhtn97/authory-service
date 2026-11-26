package com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity.TokenEntity;

@Repository
public interface TokenJpaRepository
        extends JpaRepository<TokenEntity, UUID>, JpaSpecificationExecutor<TokenEntity> {

    @Transactional
    @Modifying
    @Query(
            "UPDATE TokenEntity t SET t.revoked = true WHERE t.userId = :userId AND t.revoked = false")
    void revokeAllByUserId(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(
            "UPDATE TokenEntity t SET t.revoked = true WHERE t.userId = :userId AND t.id != :excludedTokenId AND t.revoked = false")
    void revokeAllByUserIdExcept(
            @Param("userId") UUID userId, @Param("excludedTokenId") UUID excludedTokenId);

    @Transactional
    @Modifying
    @Query("UPDATE TokenEntity t SET t.revoked = true WHERE t.userId = :userId AND t.id = :tokenId")
    void revokeSession(@Param("userId") UUID userId, @Param("tokenId") UUID tokenId);

    java.util.List<TokenEntity> findByUserIdAndTypeAndRevokedIsFalse(UUID userId, String type);

    Optional<TokenEntity> findByJtiAndRevokedIsFalse(String jti);

    long countByRevokedIsFalse();
}
