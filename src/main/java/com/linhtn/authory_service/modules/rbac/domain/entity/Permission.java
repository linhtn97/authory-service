package com.linhtn.authory_service.modules.rbac.domain.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.UuidGenerator;

import com.linhtn.shared_kernel.infrastructure.jpa.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "permissions",
        schema = "rbac",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"code", "action"})})
@Getter
@Setter
public class Permission extends BaseEntity {
    @Id @GeneratedValue @UuidGenerator private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String name;
}
