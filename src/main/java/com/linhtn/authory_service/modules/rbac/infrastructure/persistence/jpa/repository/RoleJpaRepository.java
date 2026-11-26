package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.RoleEntity;

@Repository
public interface RoleJpaRepository
        extends JpaRepository<RoleEntity, UUID>, JpaSpecificationExecutor<RoleEntity> {}
