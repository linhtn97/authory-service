package com.linhtn.authory_service.modules.activity.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.activity.domain.entity.SystemActivity;

@Repository
public interface SystemActivityRepository extends JpaRepository<SystemActivity, UUID> {
    @Query("SELECT a FROM SystemActivity a ORDER BY a.createdAt DESC")
    List<SystemActivity> findRecentActivities(Pageable pageable);
}
