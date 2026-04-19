package com.linhtn.authory_service.modules.organization.repository;

import com.linhtn.authory_service.modules.organization.domain.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID>, JpaSpecificationExecutor<Position> {
}
