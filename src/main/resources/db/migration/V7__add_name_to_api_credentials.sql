-- ==============================================================================
-- Migration: V7__add_name_to_api_credentials.sql
-- Description: Add descriptive name column and composite index to auth.api_credentials
-- ==============================================================================

ALTER TABLE auth.api_credentials
    ADD COLUMN IF NOT EXISTS name VARCHAR(100) NOT NULL DEFAULT 'Default API Key';

CREATE INDEX IF NOT EXISTS idx_api_credentials_user_revoked
    ON auth.api_credentials(user_id, revoked);
