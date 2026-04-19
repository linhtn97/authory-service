package com.linhtn.authory_service.modules.auth.domain.entity;

import com.linhtn.shared_kernel.infrastructure.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_credentials", schema = "partner")
@Getter
@Setter
public class ApiCredential extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @Column(nullable = false)
    private String clientId;
    @Column(nullable = false)
    private String hashedSecret;
    @Column(nullable = false)
    private String apiKey;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private Boolean revoked = false;
    @Column(nullable = false)
    private LocalDateTime expiredAt;
    @Column(nullable = false)
    private UUID partnerId;
}
