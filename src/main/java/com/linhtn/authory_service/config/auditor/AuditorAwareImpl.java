package com.linhtn.authory_service.config.auditor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;

import com.linhtn.authory_service.config.security.util.AccessPrincipalUtil;

public class AuditorAwareImpl implements AuditorAware<UUID> {
    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(AccessPrincipalUtil.getId());
    }
}
