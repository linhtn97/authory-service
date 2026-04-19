package com.linhtn.authory_service.modules.rbac.repository;

import com.linhtn.authory_service.modules.rbac.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID>, JpaSpecificationExecutor<UserRole> {

    @Query("SELECT ur.role.id FROM UserRole ur WHERE ur.userId = :userId")
    Set<UUID> findRoleIdsByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(UUID userId);

    @Modifying
    @Query("""
        DELETE FROM UserRole ur
        WHERE ur.userId = :userId
        AND ur.role.id IN :roleIds
    """)
    void deleteByUserIdAndRoleIds(UUID userId, Set<UUID> roleIds);
}
