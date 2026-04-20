package com.linhtn.authory_service.modules.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.auth.domain.entity.Token;

@Repository
public interface TokenRepository
        extends JpaRepository<Token, UUID>, JpaSpecificationExecutor<Token> {
    @Transactional
    @Modifying
    @Query("UPDATE Token t SET t.revoked = true WHERE t.userId = :userId AND t.revoked = false")
    void revokeAllByUserId(@Param("userId") UUID userId);

    Optional<Token> findByJtiAndRevokedIsFalse(String jti);

    long countByRevokedIsFalse();
}
