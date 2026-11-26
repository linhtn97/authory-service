package com.linhtn.authory_service.config.auditor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;

import com.linhtn.authory_service.config.security.util.CurrentPrincipalUtil;

public class AuditorAwareImpl implements AuditorAware<UUID> {
    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(
                CurrentPrincipalUtil.getId() == null ? null : CurrentPrincipalUtil.getId());
    }
}
