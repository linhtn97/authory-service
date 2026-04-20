package com.linhtn.authory_service.modules.rbac.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.Group;

@Repository
public interface GroupRepository
        extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {}
