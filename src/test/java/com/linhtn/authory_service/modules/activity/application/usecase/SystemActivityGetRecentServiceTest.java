package com.linhtn.authory_service.modules.activity.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.activity.application.dto.result.SystemActivityGetRecentResult;
import com.linhtn.authory_service.modules.activity.domain.model.SystemActivity;
import com.linhtn.authory_service.modules.activity.domain.repository.SystemActivityRepository;

@ExtendWith(MockitoExtension.class)
class SystemActivityGetRecentServiceTest {

    @Mock private SystemActivityRepository systemActivityRepository;

    private SystemActivityGetRecentService service;

    @BeforeEach
    void setUp() {
        service = new SystemActivityGetRecentService(systemActivityRepository);
    }

    @Test
    @DisplayName("Should return recent system activities")
    void shouldReturnRecentActivities() {
        UUID id = UUID.randomUUID();
        SystemActivity activity =
                SystemActivity.builder().id(id).text("User created").iconType("USER").build();

        when(systemActivityRepository.findRecentActivities(5)).thenReturn(List.of(activity));

        List<SystemActivityGetRecentResult> results = service.execute(5);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getId()).isEqualTo(id);
        assertThat(results.getFirst().getText()).isEqualTo("User created");
    }
}
