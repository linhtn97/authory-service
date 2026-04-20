package com.linhtn.authory_service.modules.abac.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.abac.domain.entity.Attribute;

@Repository
public interface AttributeRepository
        extends JpaRepository<Attribute, UUID>, JpaSpecificationExecutor<Attribute> {}
