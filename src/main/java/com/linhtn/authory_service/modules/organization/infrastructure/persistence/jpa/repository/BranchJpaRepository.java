package com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.BranchEntity;

@Repository
public interface BranchJpaRepository
        extends JpaRepository<BranchEntity, UUID>, JpaSpecificationExecutor<BranchEntity> {}
