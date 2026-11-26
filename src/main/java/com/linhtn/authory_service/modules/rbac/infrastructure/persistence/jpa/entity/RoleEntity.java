package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.UuidGenerator;

import com.linhtn.authory_service.infrastructure.persistence.jpa.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles", schema = "rbac")
@Getter
@Setter
public class RoleEntity extends BaseEntity {
    @Id @GeneratedValue @UuidGenerator private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;
}
