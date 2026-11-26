package com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.entity.UserAttributeEntity;

@Repository
public interface UserAttributeJpaRepository
        extends JpaRepository<UserAttributeEntity, UUID>,
                JpaSpecificationExecutor<UserAttributeEntity> {

    List<UserAttributeEntity> findByUserId(UUID userId);
}
