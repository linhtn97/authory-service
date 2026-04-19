package com.linhtn.authory_service.modules.activity.domain.entity;

import com.linhtn.shared_kernel.infrastructure.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "system_activities", schema = "auth")
@Getter
@Setter
public class SystemActivity extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String iconType;
}
