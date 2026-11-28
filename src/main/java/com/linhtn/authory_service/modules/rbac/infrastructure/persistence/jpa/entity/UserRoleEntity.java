package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.UuidGenerator;

import com.linhtn.authory_service.infrastructure.persistence.jpa.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "user_roles",
        schema = "rbac",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})})
@Getter
@Setter
public class UserRoleEntity extends BaseEntity {
    @Id @GeneratedValue @UuidGenerator private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;
}
