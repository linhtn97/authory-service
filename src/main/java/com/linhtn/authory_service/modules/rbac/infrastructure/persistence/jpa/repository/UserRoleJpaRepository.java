package com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserRoleEntity;

@Repository
public interface UserRoleJpaRepository
        extends JpaRepository<UserRoleEntity, UUID>, JpaSpecificationExecutor<UserRoleEntity> {

    @Query("SELECT ur.roleId FROM UserRoleEntity ur WHERE ur.userId = :userId")
    Set<UUID> findRoleIdsByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM UserRoleEntity ur WHERE ur.userId = :userId")
    void deleteByUserId(UUID userId);

    @Modifying
    @Query(
            """
        DELETE FROM UserRoleEntity ur
        WHERE ur.userId = :userId
        AND ur.roleId IN :roleIds
    """)
    void deleteByUserIdAndRoleIds(UUID userId, Set<UUID> roleIds);
}
