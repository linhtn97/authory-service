package com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.organization.infrastructure.persistence.jpa.entity.CompanyEntity;

@Repository
public interface CompanyJpaRepository
        extends JpaRepository<CompanyEntity, UUID>, JpaSpecificationExecutor<CompanyEntity> {}
