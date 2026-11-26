-- ==============================================================================
-- Migration: V3__enterprise_iam_features.sql
-- Description: Adds account lockout, MFA credentials, active session tracking,
--              and password reset tokens.
-- ==============================================================================

-- 1. Account Lockout & MFA in auth.user_credentials
ALTER TABLE auth.user_credentials
    ADD COLUMN IF NOT EXISTS failed_login_attempts INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP WITH TIME ZONE NULL,
    ADD COLUMN IF NOT EXISTS mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS mfa_secret VARCHAR(255) NULL;

-- 2. Enhanced Active Session Tracking in auth.tokens
ALTER TABLE auth.tokens
    ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45) NULL,
    ADD COLUMN IF NOT EXISTS user_agent VARCHAR(500) NULL,
    ADD COLUMN IF NOT EXISTS device_name VARCHAR(100) NULL,
    ADD COLUMN IF NOT EXISTS last_active_at TIMESTAMP WITH TIME ZONE NULL;

CREATE INDEX IF NOT EXISTS idx_tokens_user_revoked ON auth.tokens(user_id, revoked);

-- 3. Password Reset Tokens
CREATE TABLE IF NOT EXISTS auth.password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_password_reset_user ON auth.password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_hash ON auth.password_reset_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_password_reset_expires ON auth.password_reset_tokens(expires_at);

