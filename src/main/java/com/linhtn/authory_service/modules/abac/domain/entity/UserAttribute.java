package com.linhtn.authory_service.modules.abac.domain.entity;

import com.linhtn.shared_kernel.infrastructure.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "user_attributes",
        schema = "auth",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "key"})
        }
)
@Getter
@Setter
public class UserAttribute extends BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private UUID userId;
}
