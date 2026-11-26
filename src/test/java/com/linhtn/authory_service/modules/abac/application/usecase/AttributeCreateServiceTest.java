package com.linhtn.authory_service.modules.abac.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linhtn.authory_service.modules.abac.application.dto.command.AttributeCreateCommand;
import com.linhtn.authory_service.modules.abac.application.dto.result.AttributeCreateResult;
import com.linhtn.authory_service.modules.abac.domain.model.Attribute;
import com.linhtn.authory_service.modules.abac.domain.repository.AttributeRepository;

@ExtendWith(MockitoExtension.class)
class AttributeCreateServiceTest {

    @Mock private AttributeRepository attributeRepository;

    private AttributeCreateService service;

    @BeforeEach
    void setUp() {
        service = new AttributeCreateService(attributeRepository);
    }

    @Test
    @DisplayName("Should create attribute successfully")
    void shouldCreateAttributeSuccessfully() {
        UUID id = UUID.randomUUID();
        AttributeCreateCommand command =
                AttributeCreateCommand.builder()
                        .key("department")
                        .name("Department")
                        .description("Department attribute")
                        .build();

        Attribute created =
                Attribute.builder()
                        .id(id)
                        .key("department")
                        .name("Department")
                        .description("Department attribute")
                        .build();

        when(attributeRepository.create(any(Attribute.class))).thenReturn(created);

        AttributeCreateResult result = service.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getKey()).isEqualTo("department");
        verify(attributeRepository).create(any(Attribute.class));
    }
}
