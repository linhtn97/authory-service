package com.linhtn.authory_service.config.security.filter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.linhtn.authory_service.config.security.config.SecurityProperties;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.security_core.model.AuthenticatedUser;
import com.linhtn.web.i18n.MessageResolver;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @Mock private MessageResolver i18nHelper;
    @Mock private UserAuthenticationUseCase userAuthenticationUseCase;
    @Mock private ObjectMapper objectMapper;
    @Mock private SecurityProperties securityProperties;
    @Mock private FilterChain filterChain;

    private JwtAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        filter =
                new JwtAuthorizationFilter(
                        i18nHelper, userAuthenticationUseCase, objectMapper, securityProperties);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should pass through when no Authorization header is present")
    void shouldPassThroughWhenNoAuthHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName(
            "Should authenticate user and set SecurityContext when valid Bearer token provided")
    void shouldAuthenticateUserWhenValidTokenProvided() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users");
        request.addHeader("Authorization", "Bearer valid-token-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticatedUser user =
                AuthenticatedUser.builder()
                        .userId(UUID.randomUUID().toString())
                        .username("john_doe")
                        .email("john@example.com")
                        .roles(Set.of("ROLE_USER"))
                        .groups(Set.of("GRP_1"))
                        .permissions(Set.of("user:read"))
                        .build();

        when(userAuthenticationUseCase.getPrincipal("valid-token-123")).thenReturn(user);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should authenticate user when valid API key header provided")
    void shouldAuthenticateUserWhenApiKeyPresent() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users");
        request.addHeader("X-API-KEY", "ak_12345");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticatedUser user =
                AuthenticatedUser.builder()
                        .userId(UUID.randomUUID().toString())
                        .username("api_user")
                        .email("api@user.com")
                        .roles(Set.of("ROLE_USER"))
                        .build();

        when(userAuthenticationUseCase.getPrincipalByApiKey("ak_12345")).thenReturn(user);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should authenticate user when valid client credentials provided")
    void shouldAuthenticateUserWhenClientHeadersPresent() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/users");
        request.addHeader("Client-Id", "client-id-1");
        request.addHeader("Client-Key", "client-key-1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticatedUser user =
                AuthenticatedUser.builder()
                        .userId(UUID.randomUUID().toString())
                        .username("client_user")
                        .email("client@user.com")
                        .roles(Set.of("ROLE_USER"))
                        .build();

        when(userAuthenticationUseCase.getPrincipalByClientCredentials(
                        "client-id-1", "client-key-1"))
                .thenReturn(user);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
