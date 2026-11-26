package com.linhtn.authory_service.modules.abac.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeGetResult;
import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;
import com.linhtn.authory_service.modules.abac.exception.unchecked.AttributeNotFoundException;
import com.linhtn.web.i18n.MessageResolver;

@ExtendWith(MockitoExtension.class)
class AttributeGetServiceTest {

    @Mock private AttributeRepository attributeRepository;
    @Mock private MessageResolver i18nHelper;

    private AttributeGetService service;

    @BeforeEach
    void setUp() {
        service = new AttributeGetService(attributeRepository, i18nHelper);
    }

    @Test
    @DisplayName("Should return AttributeResult when attribute exists")
    void shouldReturnAttributeWhenFound() {
        UUID id = UUID.randomUUID();
        Attribute attribute =
                Attribute.builder().id(id).key("clearance").name("Security Clearance").build();

        when(attributeRepository.get(id)).thenReturn(Optional.of(attribute));

        AttributeGetResult result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getKey()).isEqualTo("clearance");
    }

    @Test
    @DisplayName("Should throw AttributeNotFoundException when attribute does not exist")
    void shouldThrowWhenAttributeNotFound() {
        UUID id = UUID.randomUUID();
        when(attributeRepository.get(id)).thenReturn(Optional.empty());
        when(i18nHelper.getMessage(any(), eq(id))).thenReturn("Attribute not found");

        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(AttributeNotFoundException.class);
    }
}
