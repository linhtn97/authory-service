package com.linhtn.authory_service.modules.abac.repository;

import com.linhtn.authory_service.modules.abac.domain.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttributeRepository
        extends JpaRepository<Attribute, UUID>, JpaSpecificationExecutor<Attribute> {
}
