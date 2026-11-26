package com.linhtn.authory_service.modules.user.application.usecase;

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

import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.domain.model.User;
import com.linhtn.authory_service.modules.user.domain.repository.UserRepository;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

@ExtendWith(MockitoExtension.class)
class UserGetPageServiceTest {

    @Mock private UserRepository userRepository;

    private UserGetPageService service;

    @BeforeEach
    void setUp() {
        service = new UserGetPageService(userRepository);
    }

    @Test
    @DisplayName("Should return paged user results")
    void shouldReturnPagedUsers() {
        PagingRequest request = PagingRequest.builder().page(0).pageSize(10).build();
        User user =
                User.builder()
                        .id(UUID.randomUUID())
                        .username("johndoe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .build();

        PagingResponse<User> pagedUsers = PagingResponse.of(List.of(user), 1, 10, 1);

        when(userRepository.getPage(request)).thenReturn(pagedUsers);

        PagingResponse<UserGetResult> result = service.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getUsername()).isEqualTo("johndoe");
        assertThat(result.getTotalItems()).isEqualTo(1);
    }
}
