package com.linhtn.authory_service.modules.activity.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linhtn.authory_service.modules.activity.application.dto.result.SystemActivityGetRecentResult;
import com.linhtn.authory_service.modules.activity.application.mapper.SystemActivityApplicationMapper;
import com.linhtn.authory_service.modules.activity.application.port.in.SystemActivityGetRecentUseCase;
import com.linhtn.authory_service.modules.activity.domain.model.SystemActivity;
import com.linhtn.authory_service.modules.activity.domain.repository.SystemActivityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemActivityGetRecentService implements SystemActivityGetRecentUseCase {
    private final SystemActivityRepository systemActivityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SystemActivityGetRecentResult> execute(int limit) {
        List<SystemActivity> activities = systemActivityRepository.findRecentActivities(limit);
        return SystemActivityApplicationMapper.toResults(activities);
    }
}
