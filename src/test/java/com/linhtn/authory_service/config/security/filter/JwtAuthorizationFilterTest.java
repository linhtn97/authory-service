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
import com.linhtn.authory_service.config.security.model.Principal;
import com.linhtn.authory_service.config.security.model.PrincipalAuthorities;
import com.linhtn.authory_service.modules.auth.application.port.in.PartnerAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.web.i18n.MessageResolver;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

    @Mock private MessageResolver i18nHelper;
    @Mock private PartnerAuthenticationUseCase partnerAuthenticationUseCase;
    @Mock private UserAuthenticationUseCase userAuthenticationUseCase;
    @Mock private ObjectMapper objectMapper;
    @Mock private SecurityProperties securityProperties;
    @Mock private FilterChain filterChain;

    private JwtAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        filter =
                new JwtAuthorizationFilter(
                        i18nHelper,
                        partnerAuthenticationUseCase,
                        userAuthenticationUseCase,
                        objectMapper,
                        securityProperties);
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

        PrincipalAuthorities authorities =
                new PrincipalAuthorities(
                        Set.of("ROLE_USER"), Set.of("GRP_1"), Set.of(), Set.of("user:read"));
        Principal principal =
                new Principal(
                        UUID.randomUUID(),
                        "john_doe",
                        "john@example.com",
                        "John",
                        "Doe",
                        authorities);

        when(userAuthenticationUseCase.getPrincipal("valid-token-123")).thenReturn(principal);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should authenticate partner when valid client credentials provided")
    void shouldAuthenticatePartnerWhenClientHeadersPresent() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/partners/profile");
        request.addHeader("Client-Id", "partner-id-1");
        request.addHeader("Client-Key", "partner-key-1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        PrincipalAuthorities authorities =
                new PrincipalAuthorities(Set.of("ROLE_PARTNER"), Set.of(), Set.of(), Set.of());
        Principal principal =
                new Principal(
                        UUID.randomUUID(),
                        "partner_client",
                        "partner@client.com",
                        "Partner",
                        "Client",
                        authorities);

        when(partnerAuthenticationUseCase.getPrincipal("partner-id-1", "partner-key-1"))
                .thenReturn(principal);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
