package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.UserRole;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserRoleRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserRoleEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.RoleJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.UserRoleJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.UserRoleEntityMapper;

@Repository
public class UserRoleRepositoryAdapter implements UserRoleRepository {

    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    public UserRoleRepositoryAdapter(
            UserRoleJpaRepository userRoleJpaRepository, RoleJpaRepository roleJpaRepository) {
        this.userRoleJpaRepository = userRoleJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Set<UUID> findRoleIdsByUserId(UUID userId) {
        return userRoleJpaRepository.findRoleIdsByUserId(userId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userRoleJpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndRoleIds(UUID userId, Set<UUID> roleIds) {
        userRoleJpaRepository.deleteByUserIdAndRoleIds(userId, roleIds);
    }

    @Override
    public UserRole save(UserRole userRole) {
        UserRoleEntity entity = new UserRoleEntity();
        entity.setId(userRole.getId());
        entity.setUserId(userRole.getUserId());
        if (userRole.getRole() != null && userRole.getRole().getId() != null) {
            entity.setRole(roleJpaRepository.getReferenceById(userRole.getRole().getId()));
        }
        UserRoleEntity saved = userRoleJpaRepository.save(entity);
        return UserRoleEntityMapper.toDomain(saved);
    }

    @Override
    public List<UserRole> saveAll(List<UserRole> userRoles) {
        List<UserRoleEntity> entities =
                userRoles.stream()
                        .map(
                                ur -> {
                                    UserRoleEntity entity = new UserRoleEntity();
                                    entity.setId(ur.getId());
                                    entity.setUserId(ur.getUserId());
                                    if (ur.getRole() != null && ur.getRole().getId() != null) {
                                        entity.setRole(
                                                roleJpaRepository.getReferenceById(
                                                        ur.getRole().getId()));
                                    }
                                    return entity;
                                })
                        .toList();
        return userRoleJpaRepository.saveAll(entities).stream()
                .map(UserRoleEntityMapper::toDomain)
                .toList();
    }
}
