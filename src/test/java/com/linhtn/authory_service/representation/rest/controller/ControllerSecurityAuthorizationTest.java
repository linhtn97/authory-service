package com.linhtn.authory_service.representation.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.linhtn.authory_service.config.security.config.SecurityCoreBridgeConfig;
import com.linhtn.authory_service.modules.auth.application.port.in.UserAuthenticationUseCase;
import com.linhtn.authory_service.modules.auth.representation.rest.controller.PrincipalController;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleCreateUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleDeleteUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetPageUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleGetUseCase;
import com.linhtn.authory_service.modules.rbac.application.port.in.RoleUpdateUseCase;
import com.linhtn.authory_service.modules.rbac.representation.rest.controller.RoleController;
import com.linhtn.authory_service.modules.user.application.dto.result.UserGetResult;
import com.linhtn.authory_service.modules.user.application.port.in.UserCreateUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserDeleteUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetPageUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserGetUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserUnlockUseCase;
import com.linhtn.authory_service.modules.user.application.port.in.UserUpdateUseCase;
import com.linhtn.authory_service.modules.user.representation.rest.controller.UserController;
import com.linhtn.security_core.aspect.SecurityAuthorizationAspect;
import com.linhtn.security_core.exception.AccessDeniedException;
import com.linhtn.security_core.exception.AuthenticationRequiredException;
import com.linhtn.security_core.model.AuthenticatedUser;
import com.linhtn.security_core.port.UserContextProvider;
import com.linhtn.security_core.service.DefaultPermissionEvaluator;

@DisplayName("Controller Declarative Security Authorization Edge-Case Tests")
class ControllerSecurityAuthorizationTest {

    private UserController userControllerProxy;
    private RoleController roleControllerProxy;
    private PrincipalController principalControllerProxy;

    private UserGetUseCase userGetUseCase;
    private UserDeleteUseCase userDeleteUseCase;
    private UserUnlockUseCase userUnlockUseCase;
    private UserAuthenticationUseCase userAuthenticationUseCase;

    @BeforeEach
    void setUp() {
        userGetUseCase = mock(UserGetUseCase.class);
        userDeleteUseCase = mock(UserDeleteUseCase.class);
        userUnlockUseCase = mock(UserUnlockUseCase.class);
        userAuthenticationUseCase = mock(UserAuthenticationUseCase.class);

        UserController targetUserController =
                new UserController(
                        mock(UserCreateUseCase.class),
                        userGetUseCase,
                        mock(UserGetPageUseCase.class),
                        mock(UserUpdateUseCase.class),
                        userDeleteUseCase,
                        userUnlockUseCase);

        RoleController targetRoleController =
                new RoleController(
                        mock(RoleCreateUseCase.class),
                        mock(RoleUpdateUseCase.class),
                        mock(RoleGetUseCase.class),
                        mock(RoleGetPageUseCase.class),
                        mock(RoleDeleteUseCase.class));

        PrincipalController targetPrincipalController =
                new PrincipalController(userAuthenticationUseCase);

        UserContextProvider userContextProvider =
                new SecurityCoreBridgeConfig().userContextProvider();
        SecurityAuthorizationAspect aspect =
                new SecurityAuthorizationAspect(
                        new DefaultPermissionEvaluator(), userContextProvider);

        AspectJProxyFactory userFactory = new AspectJProxyFactory(targetUserController);
        userFactory.addAspect(aspect);
        userControllerProxy = userFactory.getProxy();

        AspectJProxyFactory roleFactory = new AspectJProxyFactory(targetRoleController);
        roleFactory.addAspect(aspect);
        roleControllerProxy = roleFactory.getProxy();

        AspectJProxyFactory principalFactory = new AspectJProxyFactory(targetPrincipalController);
        principalFactory.addAspect(aspect);
        principalControllerProxy = principalFactory.getProxy();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        com.linhtn.security_core.context.SecurityContextHolder.clearContext();
    }

    private void authenticate(Set<String> roles, Set<String> groups, Set<String> permissions) {
        AuthenticatedUser user =
                AuthenticatedUser.builder()
                        .userId(UUID.randomUUID().toString())
                        .username("testuser")
                        .email("test@example.com")
                        .roles(roles != null ? roles : Collections.emptySet())
                        .groups(groups != null ? groups : Collections.emptySet())
                        .permissions(permissions != null ? permissions : Collections.emptySet())
                        .build();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    @DisplayName(
            "Edge Case 1: Unauthenticated request to UserController.get() throws AuthenticationRequiredException")
    void testUnauthenticatedGetThrows() {
        UUID targetId = UUID.randomUUID();
        assertThatThrownBy(() -> userControllerProxy.get(targetId))
                .isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    @DisplayName(
            "Edge Case 2: User with USER role but lacking USER:READ permission is denied on UserController.get()")
    void testMissingPermissionDenied() {
        authenticate(Set.of("USER"), Set.of(), Set.of("PROFILE:VIEW"));
        UUID targetId = UUID.randomUUID();

        assertThatThrownBy(() -> userControllerProxy.get(targetId))
                .isInstanceOf(AccessDeniedException.class)
                .satisfies(
                        ex -> {
                            AccessDeniedException ade = (AccessDeniedException) ex;
                            assertThat(ade.getMissingAuthorities()).contains("USER:READ");
                        });
    }

    @Test
    @DisplayName(
            "Edge Case 3: User with exact USER:READ permission is permitted on UserController.get()")
    void testExactPermissionPermitted() {
        authenticate(Set.of("USER"), Set.of(), Set.of("USER:READ"));
        UUID targetId = UUID.randomUUID();

        UserGetResult mockResult =
                UserGetResult.builder()
                        .id(targetId)
                        .username("testuser")
                        .email("test@example.com")
                        .build();
        when(userGetUseCase.execute(targetId)).thenReturn(mockResult);

        var response = userControllerProxy.get(targetId);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(targetId);
    }

    @Test
    @DisplayName(
            "Edge Case 4: User with wildcard superuser permission '*' or 'USER:*' is permitted on UserController.get()")
    void testWildcardPermissionPermitted() {
        authenticate(Set.of("USER"), Set.of(), Set.of("USER:*"));
        UUID targetId = UUID.randomUUID();

        UserGetResult mockResult =
                UserGetResult.builder()
                        .id(targetId)
                        .username("testuser")
                        .email("test@example.com")
                        .build();
        when(userGetUseCase.execute(targetId)).thenReturn(mockResult);

        var response = userControllerProxy.get(targetId);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName(
            "Edge Case 5: Standard user lacking ADMIN role is denied on RoleController (Class-Level @RequireRole)")
    void testClassLevelRoleDenied() {
        authenticate(Set.of("USER"), Set.of(), Set.of("USER:READ"));
        UUID roleId = UUID.randomUUID();

        assertThatThrownBy(() -> roleControllerProxy.get(roleId))
                .isInstanceOf(AccessDeniedException.class)
                .satisfies(
                        ex -> {
                            AccessDeniedException ade = (AccessDeniedException) ex;
                            assertThat(ade.getMissingAuthorities()).contains("ROLE_ADMIN");
                        });
    }

    @Test
    @DisplayName("Edge Case 6: Admin user is permitted on RoleController")
    void testClassLevelRolePermitted() {
        authenticate(Set.of("ADMIN"), Set.of(), Set.of());
        UUID roleId = UUID.randomUUID();

        // Method execution passes security check without throwing authorization errors
        assertThatCode(() -> roleControllerProxy.get(roleId)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
            "Edge Case 7: Method with @RequireRole({'ADMIN', 'SUPER_ADMIN'}) on UserController.unlock() passes for SUPER_ADMIN")
    void testUnlockPermittedForSuperAdmin() {
        authenticate(Set.of("SUPER_ADMIN"), Set.of(), Set.of());
        UUID userId = UUID.randomUUID();

        userControllerProxy.unlock(userId);
    }

    @Test
    @DisplayName(
            "Edge Case 8: Method with @RequireRole({'ADMIN', 'SUPER_ADMIN'}) on UserController.unlock() denies USER role")
    void testUnlockDeniedForUser() {
        authenticate(Set.of("USER"), Set.of(), Set.of());
        UUID userId = UUID.randomUUID();

        assertThatThrownBy(() -> userControllerProxy.unlock(userId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName(
            "Edge Case 9: PrincipalController.getPrincipal() throws AuthenticationRequiredException when unauthenticated")
    void testPrincipalUnauthenticatedThrows() {
        // No authentication set
        assertThatThrownBy(() -> principalControllerProxy.getPrincipal("Bearer some-token"))
                .isInstanceOf(AuthenticationRequiredException.class);
    }

    @Test
    @DisplayName(
            "Edge Case 10: PrincipalController.getPrincipal() succeeds for authenticated user with USER role")
    void testPrincipalAuthenticatedSucceeds() {
        authenticate(Set.of("USER"), Set.of(), Set.of());
        when(userAuthenticationUseCase.getPrincipal("some-token"))
                .thenReturn(AuthenticatedUser.builder().username("testuser").build());

        assertThatCode(() -> principalControllerProxy.getPrincipal("Bearer some-token"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
            "Edge Case 11: PrincipalController.getPrincipalByClientCredentials() succeeds unauthenticated because of @PublicEndpoint")
    void testClientPrincipalPermittedUnauthenticated() {
        when(userAuthenticationUseCase.getPrincipalByClientCredentials("client-id", "client-key"))
                .thenReturn(AuthenticatedUser.builder().username("client-user").build());

        assertThatCode(
                        () ->
                                principalControllerProxy.getPrincipalByClientCredentials(
                                        "client-id", "client-key"))
                .doesNotThrowAnyException();
    }
}
