package com.linhtn.authory_service.modules.abac.repository;

import com.linhtn.authory_service.modules.abac.domain.entity.Attribute;
import com.linhtn.authory_service.modules.abac.domain.entity.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, UUID>, JpaSpecificationExecutor<UserAttribute> {
}
