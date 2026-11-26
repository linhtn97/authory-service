package com.linhtn.authory_service.modules.partner.infrastructure.persistence.jpa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.partner.infrastructure.persistence.jpa.entity.PartnerEntity;

@Repository
public interface PartnerJpaRepository
        extends JpaRepository<PartnerEntity, UUID>, JpaSpecificationExecutor<PartnerEntity> {}
