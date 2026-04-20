package com.linhtn.authory_service.modules.organization.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.organization.domain.entity.Branch;

@Repository
public interface BranchRepository
        extends JpaRepository<Branch, UUID>, JpaSpecificationExecutor<Branch> {}
