package com.linhtn.authory_service.modules.activity.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.activity.infrastructure.persistence.jpa.entity.SystemActivityEntity;

@Repository
public interface SystemActivityJpaRepository extends JpaRepository<SystemActivityEntity, UUID> {
    @Query("SELECT a FROM SystemActivityEntity a ORDER BY a.createdAt DESC")
    List<SystemActivityEntity> findRecentActivities(Pageable pageable);
}
