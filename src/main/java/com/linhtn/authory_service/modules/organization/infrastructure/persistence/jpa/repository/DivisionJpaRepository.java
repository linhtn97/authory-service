package com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.DivisionEntity;

@Repository
public interface DivisionJpaRepository
        extends JpaRepository<DivisionEntity, UUID>, JpaSpecificationExecutor<DivisionEntity> {}
