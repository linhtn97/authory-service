package com.linhtn.authory_service.modules.auth.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.UuidGenerator;

import com.linhtn.authory_service.infrastructure.persistence.jpa.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_credentials", schema = "auth")
public class ApiCredentialEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name = "Default API Key";

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

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
