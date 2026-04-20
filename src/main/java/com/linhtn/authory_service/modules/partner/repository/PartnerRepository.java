package com.linhtn.authory_service.modules.partner.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.partner.domain.entity.Partner;

@Repository
public interface PartnerRepository
        extends JpaRepository<Partner, UUID>, JpaSpecificationExecutor<Partner> {}
