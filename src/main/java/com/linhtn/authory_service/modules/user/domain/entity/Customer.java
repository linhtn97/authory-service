package com.linhtn.authory_service.modules.user.domain.entity;

import com.linhtn.shared_kernel.infrastructure.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "customers", schema = "organization")
@Getter
@Setter
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    private String code;
    private UUID branchId;
    private UUID userId;
}
