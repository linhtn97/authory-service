package com.linhtn.authory_service.modules.activity.infrastructure.persistence;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.linhtn.authory_service.modules.activity.domain.model.SystemActivity;
import com.linhtn.authory_service.modules.activity.domain.repository.SystemActivityRepository;
import com.linhtn.authory_service.modules.activity.infrastructure.persistence.jpa.entity.SystemActivityEntity;
import com.linhtn.authory_service.modules.activity.infrastructure.persistence.jpa.repository.SystemActivityJpaRepository;
import com.linhtn.authory_service.modules.activity.infrastructure.persistence.mapper.SystemActivityEntityMapper;

@Repository
public class SystemActivityRepositoryAdapter implements SystemActivityRepository {

    private final SystemActivityJpaRepository jpaRepository;

    public SystemActivityRepositoryAdapter(SystemActivityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SystemActivity save(SystemActivity activity) {
        SystemActivityEntity entity = SystemActivityEntityMapper.toEntity(activity);
        SystemActivityEntity saved = jpaRepository.save(entity);
        return SystemActivityEntityMapper.toDomain(saved);
    }

    @Override
    public List<SystemActivity> findRecentActivities(int limit) {
        List<SystemActivityEntity> entities =
                jpaRepository.findRecentActivities(PageRequest.of(0, limit));
        return entities.stream().map(SystemActivityEntityMapper::toDomain).toList();
    }
}
