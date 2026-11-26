package com.linhtn.authory_service.config.security.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TotpUtilTest {

    @Test
    void shouldGenerateValidSecretAndOtpAuthUrl() {
        String secret = TotpUtil.generateSecretKey();
        assertNotNull(secret);
        assertFalse(secret.isBlank());

        String url = TotpUtil.getOtpAuthUrl("AuthoryService", "admin@domain.com", secret);
        assertNotNull(url);
        assertTrue(url.startsWith("otpauth://totp/AuthoryService:admin@domain.com?secret="));
        assertTrue(url.contains(secret));
    }

    @Test
    void shouldRejectInvalidCodes() {
        String secret = TotpUtil.generateSecretKey();
        assertFalse(TotpUtil.verifyCode(secret, "000000"));
        assertFalse(TotpUtil.verifyCode(secret, "invalid"));
        assertFalse(TotpUtil.verifyCode(null, "123456"));
        assertFalse(TotpUtil.verifyCode(secret, null));
    }
}
