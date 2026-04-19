package com.linhtn.authory_service.modules.activity.service.impl;

import com.linhtn.authory_service.modules.activity.domain.dto.SystemActivityDto;
import com.linhtn.authory_service.modules.activity.domain.entity.SystemActivity;
import com.linhtn.authory_service.modules.activity.domain.mapper.SystemActivityMapper;
import com.linhtn.authory_service.modules.activity.repository.SystemActivityRepository;
import com.linhtn.authory_service.modules.activity.service.SystemActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemActivityServiceImpl implements SystemActivityService {

    private final SystemActivityRepository systemActivityRepository;
    private final SystemActivityMapper systemActivityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SystemActivityDto> getRecentActivities(int limit) {
        List<SystemActivity> activities = systemActivityRepository.findRecentActivities(PageRequest.of(0, limit));
        return systemActivityMapper.toDtos(activities);
    }
}
