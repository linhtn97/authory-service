package com.linhtn.authory_service.modules.auth.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.auth.application.dto.result.TokenResult;
import com.linhtn.authory_service.modules.auth.application.mapper.TokenMapper;
import com.linhtn.authory_service.modules.auth.domain.model.Token;
import com.linhtn.authory_service.modules.auth.domain.repository.TokenRepository;
import com.linhtn.authory_service.modules.auth.exception.unchecked.TokenNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock private MessageResolver i18nHelper;
    @Mock private TokenRepository tokenRepository;
    @Mock private TokenMapper tokenMapper;

    private TokenService service;

    @BeforeEach
    void setUp() {
        service = new TokenService(i18nHelper, tokenRepository, tokenMapper);
    }

    @Test
    @DisplayName("Should create token successfully")
    void shouldCreateTokenSuccessfully() {
        UUID id = UUID.randomUUID();
        TokenResult request = TokenResult.builder().jti("jti-1").userId(UUID.randomUUID()).build();
        Token savedToken = Token.builder().id(id).jti("jti-1").build();
        TokenResult responseDto = TokenResult.builder().id(id).jti("jti-1").build();

        doAnswer(
                        invocation -> {
                            Token t = invocation.getArgument(0);
                            t.setJti("jti-1");
                            return null;
                        })
                .when(tokenMapper)
                .fillEntity(any(Token.class), any(TokenResult.class));

        when(tokenRepository.create(any(Token.class))).thenReturn(savedToken);
        when(tokenMapper.toDto(savedToken)).thenReturn(responseDto);

        TokenResult result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(tokenRepository).create(any(Token.class));
    }

    @Test
    @DisplayName("Should get token by ID")
    void shouldGetTokenById() {
        UUID id = UUID.randomUUID();
        Token token = Token.builder().id(id).jti("jti-1").build();
        TokenResult dto = TokenResult.builder().id(id).jti("jti-1").build();

        when(tokenRepository.get(id)).thenReturn(Optional.of(token));
        when(tokenMapper.toDto(token)).thenReturn(dto);

        TokenResult result = service.get(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should throw TokenNotFoundException when token does not exist")
    void shouldThrowWhenTokenNotFound() {
        UUID id = UUID.randomUUID();
        when(tokenRepository.get(id)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any())).thenReturn("Token not found");

        assertThatThrownBy(() -> service.get(id)).isInstanceOf(TokenNotFoundException.class);
    }

    @Test
    @DisplayName("Should revoke all tokens by user ID")
    void shouldRevokeAllByUserId() {
        UUID userId = UUID.randomUUID();

        service.revokeAllByUserId(userId);

        verify(tokenRepository).revokeAllByUserId(userId);
    }
}
