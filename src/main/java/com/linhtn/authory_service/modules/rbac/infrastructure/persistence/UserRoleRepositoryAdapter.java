package com.linhtn.authory_service.modules.rbac.infrastructure.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.rbac.domain.model.UserRole;
import com.linhtn.authory_service.modules.rbac.domain.repository.UserRoleRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.entity.UserRoleEntity;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.jpa.repository.UserRoleJpaRepository;
import com.linhtn.authory_service.modules.rbac.infrastructure.persistence.mapper.UserRoleEntityMapper;

@Repository
public class UserRoleRepositoryAdapter implements UserRoleRepository {

    private final UserRoleJpaRepository userRoleJpaRepository;

    public UserRoleRepositoryAdapter(UserRoleJpaRepository userRoleJpaRepository) {
        this.userRoleJpaRepository = userRoleJpaRepository;
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
        UserRoleEntity entity = UserRoleEntityMapper.toEntity(userRole);
        UserRoleEntity saved = userRoleJpaRepository.save(entity);
        return UserRoleEntityMapper.toDomain(saved);
    }

    @Override
    public List<UserRole> saveAll(List<UserRole> userRoles) {
        List<UserRoleEntity> entities =
                userRoles.stream().map(UserRoleEntityMapper::toEntity).toList();
        return userRoleJpaRepository.saveAll(entities).stream()
                .map(UserRoleEntityMapper::toDomain)
                .toList();
    }
}
