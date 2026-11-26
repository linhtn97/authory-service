package com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.abac.infrastructure.persistence.jpa.entity.AttributeEntity;

@Repository
public interface AttributeJpaRepository
        extends JpaRepository<AttributeEntity, UUID>, JpaSpecificationExecutor<AttributeEntity> {}
