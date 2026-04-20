package com.linhtn.authory_service.config.auditor;

import java.util.Optional;
import java.util.UUID;

import com.linhtn.authory_service.config.security.util.CurrentPrincipalUtil;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<UUID> {
    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(CurrentPrincipalUtil.getId() == null ? null : CurrentPrincipalUtil.getId());
    }
}
