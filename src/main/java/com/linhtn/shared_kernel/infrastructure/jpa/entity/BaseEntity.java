package com.linhtn.shared_kernel.infrastructure.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {
    @CreatedDate private LocalDateTime createdAt;
    @CreatedBy private UUID createdBy;
    @LastModifiedDate private LocalDateTime updatedAt;
    @LastModifiedBy private UUID updatedBy;
}
