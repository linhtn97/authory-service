package com.linhtn.authory_service.modules.user.domain.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.UuidGenerator;

import com.linhtn.shared_kernel.infrastructure.jpa.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employees", schema = "organization")
@Getter
@Setter
public class Employee extends BaseEntity {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    private String code;
    private String positionId;
    private UUID departmentId;
    private UUID userId;
}
