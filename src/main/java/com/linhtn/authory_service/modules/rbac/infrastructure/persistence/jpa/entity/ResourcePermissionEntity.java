package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.UuidGenerator;

import com.linhtn.authory_service.infrastructure.persistence.jpa.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "resource_permissions",
        schema = "rbac",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"resource_id", "permission_id"})})
@Getter
@Setter
public class ResourcePermissionEntity extends BaseEntity {
    @Id @GeneratedValue @UuidGenerator private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private ResourceEntity resource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private PermissionEntity permission;
}
