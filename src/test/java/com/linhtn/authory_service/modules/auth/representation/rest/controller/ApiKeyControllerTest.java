package com.linhtn.authory_service.modules.auth.representation.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.linhtn.authory_service.modules.auth.application.dto.command.ApiKeyCreateCommand;
import com.linhtn.authory_service.modules.auth.application.dto.result.ApiCredentialResult;
import com.linhtn.authory_service.modules.auth.application.port.in.ApiCredentialUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.request.ApiKeyCreateRequest;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.ApiKeyCreateResponse;
import com.linhtn.authory_service.modules.auth.representation.rest.dto.response.ApiKeyResponse;
import com.linhtn.security_core.model.AuthenticatedUser;
import com.linhtn.sharedkernel.pagination.PagingRequest;
import com.linhtn.sharedkernel.pagination.PagingResponse;

@ExtendWith(MockitoExtension.class)
class ApiKeyControllerTest {

    @Mock private ApiCredentialUseCase apiCredentialUseCase;

    private ApiKeyController controller;
    private AuthenticatedUser currentUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        controller = new ApiKeyController(apiCredentialUseCase);
        userId = UUID.randomUUID();
        currentUser =
                AuthenticatedUser.builder()
                        .userId(userId.toString())
                        .username("test_user")
                        .roles(Set.of("ROLE_USER"))
                        .build();
    }

    @Test
    @DisplayName("Should issue API key for current user")
    void shouldIssueApiKey() {
        ApiKeyCreateRequest request = ApiKeyCreateRequest.builder().name("Test Key").build();

        ApiCredentialResult result =
                ApiCredentialResult.builder()
                        .id(UUID.randomUUID())
                        .name("Test Key")
                        .clientId("client-123")
                        .hashedSecret("secret-456")
                        .apiKey("ak_1234567890abcdef")
                        .status("ACTIVE")
                        .expiredAt(LocalDateTime.now().plusYears(1))
                        .createdAt(LocalDateTime.now())
                        .build();

        when(apiCredentialUseCase.issue(any(ApiKeyCreateCommand.class))).thenReturn(result);

        ResponseEntity<ApiKeyCreateResponse> response =
                controller.issueApiKey(currentUser, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Key");
        assertThat(response.getBody().getApiKey()).isEqualTo("ak_1234567890abcdef");
        assertThat(response.getBody().getClientKey()).isEqualTo("secret-456");
    }

    @Test
    @DisplayName("Should get my API keys with masked key")
    void shouldGetMyApiKeys() {
        ApiCredentialResult result =
                ApiCredentialResult.builder()
                        .id(UUID.randomUUID())
                        .name("My Key")
                        .clientId("client-1")
                        .apiKey("ak_47d50664040c44b0bc3c6d414bfe59f0")
                        .status("ACTIVE")
                        .revoked(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        when(apiCredentialUseCase.getMyApiKeys(userId)).thenReturn(List.of(result));

        ResponseEntity<List<ApiKeyResponse>> response = controller.getMyApiKeys(currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("My Key");
        assertThat(response.getBody().get(0).getApiKey()).isEqualTo("ak_47d5...59f0");
    }

    @Test
    @DisplayName("Should get single API key by ID")
    void shouldGetApiKeyById() {
        UUID keyId = UUID.randomUUID();
        ApiCredentialResult result =
                ApiCredentialResult.builder()
                        .id(keyId)
                        .name("Specific Key")
                        .apiKey("ak_1111222233334444")
                        .status("ACTIVE")
                        .revoked(false)
                        .build();

        when(apiCredentialUseCase.getByIdAndUser(keyId, userId, false)).thenReturn(result);

        ResponseEntity<ApiKeyResponse> response = controller.getApiKey(keyId, currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Specific Key");
    }

    @Test
    @DisplayName("Should revoke API key")
    void shouldRevokeApiKey() {
        UUID keyId = UUID.randomUUID();

        ResponseEntity<Void> response = controller.revokeApiKey(keyId, currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(apiCredentialUseCase).revoke(keyId, userId, false);
    }

    @Test
    @DisplayName("Should rotate API key")
    void shouldRotateApiKey() {
        UUID keyId = UUID.randomUUID();
        ApiCredentialResult rotated =
                ApiCredentialResult.builder()
                        .id(keyId)
                        .name("Rotated Key")
                        .clientId("client-1")
                        .hashedSecret("new-secret")
                        .apiKey("ak_newnewnew123456")
                        .status("ACTIVE")
                        .build();

        when(apiCredentialUseCase.rotate(keyId, userId, false)).thenReturn(rotated);

        ResponseEntity<ApiKeyCreateResponse> response = controller.rotateApiKey(keyId, currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getApiKey()).isEqualTo("ak_newnewnew123456");
        assertThat(response.getBody().getClientKey()).isEqualTo("new-secret");
    }

    @Test
    @DisplayName("Should get paged API keys for Admin")
    void shouldGetPagedApiKeys() {
        PagingRequest request = new PagingRequest();
        request.setPage(0);
        request.setPageSize(10);

        ApiCredentialResult item =
                ApiCredentialResult.builder()
                        .id(UUID.randomUUID())
                        .name("Admin List Item")
                        .apiKey("ak_12345678901234")
                        .build();

        when(apiCredentialUseCase.getPage(request))
                .thenReturn(PagingResponse.of(List.of(item), 0, 10, 1));

        ResponseEntity<PagingResponse<ApiKeyResponse>> response = controller.getPage(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
    }
}
