-- =========================================================================
-- V2__seed_default_roles_and_admin.sql
-- Seed Core RBAC Roles and Default Permissions
-- =========================================================================

-- Seed Roles
INSERT INTO rbac.roles (id, code, name, created_at, updated_at)
VALUES 
    ('a0000000-0000-0000-0000-000000000001', 'SUPER_ADMIN', 'Super Administrator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000002', 'ADMIN', 'System Administrator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000003', 'USER', 'Standard User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Seed Default Resources
INSERT INTO rbac.resources (id, code, name, created_at, updated_at)
VALUES 
    ('b0000000-0000-0000-0000-000000000001', 'USER_MANAGEMENT', 'User Management Resource', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b0000000-0000-0000-0000-000000000002', 'ORGANIZATION_MANAGEMENT', 'Organization Management Resource', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b0000000-0000-0000-0000-000000000003', 'RBAC_MANAGEMENT', 'RBAC Management Resource', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

-- Seed Default Permissions
INSERT INTO rbac.permissions (id, code, action, type, name, created_at, updated_at)
VALUES 
    ('c0000000-0000-0000-0000-000000000001', 'USER:READ', 'READ', 'API', 'Read Users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c0000000-0000-0000-0000-000000000002', 'USER:WRITE', 'WRITE', 'API', 'Create/Update Users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c0000000-0000-0000-0000-000000000003', 'USER:DELETE', 'DELETE', 'API', 'Delete Users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c0000000-0000-0000-0000-000000000004', 'ORG:READ', 'READ', 'API', 'Read Organizations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c0000000-0000-0000-0000-000000000005', 'ORG:WRITE', 'WRITE', 'API', 'Manage Organizations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c0000000-0000-0000-0000-000000000006', 'RBAC:MANAGE', 'ALL', 'API', 'Manage RBAC Policies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO NOTHING;

