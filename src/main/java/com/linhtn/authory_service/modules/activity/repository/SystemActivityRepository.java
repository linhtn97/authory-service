package com.linhtn.authory_service.modules.activity.repository;

import com.linhtn.authory_service.modules.activity.domain.entity.SystemActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SystemActivityRepository extends JpaRepository<SystemActivity, UUID> {
    @Query("SELECT a FROM SystemActivity a ORDER BY a.createdAt DESC")
    List<SystemActivity> findRecentActivities(Pageable pageable);
}
