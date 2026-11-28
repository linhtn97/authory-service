-- ==============================================================================
-- Migration: V6__migrate_partner_to_user_credentials.sql
-- Description: Drop partner module schema and tables, migrate api_credentials to auth schema referencing auth.users
-- ==============================================================================

-- 1. Create auth.api_credentials table for User API keys and client credentials
CREATE TABLE IF NOT EXISTS auth.api_credentials (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    hashed_secret VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expired_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID
);

CREATE INDEX IF NOT EXISTS idx_api_credentials_user_id ON auth.api_credentials(user_id);
CREATE INDEX IF NOT EXISTS idx_api_credentials_client_id ON auth.api_credentials(client_id);
CREATE INDEX IF NOT EXISTS idx_api_credentials_api_key ON auth.api_credentials(api_key);

-- 2. Drop legacy partner RBAC tables if they exist
DROP TABLE IF EXISTS rbac.partner_roles CASCADE;
DROP TABLE IF EXISTS rbac.partner_groups CASCADE;
DROP TABLE IF EXISTS rbac.partner_resources CASCADE;

-- 3. Drop legacy partner schema tables if they exist
DROP TABLE IF EXISTS partner.api_credentials CASCADE;
DROP TABLE IF EXISTS partner.partners CASCADE;

-- 4. Drop partner schema if exists
DROP SCHEMA IF EXISTS partner CASCADE;
