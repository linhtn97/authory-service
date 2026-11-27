-- ==============================================================================
-- Migration: V5__align_partner_schema.sql
-- Description: Align partner schema with JPA entity mappings (phone, address, revoked)
-- ==============================================================================

ALTER TABLE partner.partners
    ADD COLUMN IF NOT EXISTS phone VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS address VARCHAR(500) NOT NULL DEFAULT '';

ALTER TABLE partner.api_credentials
    ADD COLUMN IF NOT EXISTS revoked BOOLEAN NOT NULL DEFAULT FALSE;
