-- =========================================================================
-- R__init_system_admin_user.sql
-- Flyway Repeatable Migration: Initialize Default System Administrator User
-- =========================================================================

-- 1. Ensure Default Admin User exists in auth.users
INSERT INTO auth.users (
    id,
    username,
    email,
    first_name,
    last_name,
    created_at,
    updated_at,
    version
)
VALUES (
    'e0000000-0000-0000-0000-000000000001',
    'admin',
    'admin@authory.internal',
    'System',
    'Administrator',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
)
ON CONFLICT (username) DO UPDATE
SET email = EXCLUDED.email,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    updated_at = CURRENT_TIMESTAMP;

-- 2. Ensure Admin User Credentials exist in auth.user_credentials
-- Password: 'Password123!' (BCrypt hash with 10 rounds)
INSERT INTO auth.user_credentials (
    id,
    user_id,
    password,
    failed_login_attempts,
    locked_until,
    mfa_enabled,
    mfa_secret,
    created_at,
    updated_at,
    version
)
SELECT 
    'e0000000-0000-0000-0000-000000000002',
    u.id,
    '$2a$10$AXIUK5kXNVaJm9WiMSJMJ.OlnS8pS1lY5rjnqzbqPKcgKD4BKS9o6',
    0,
    NULL,
    FALSE,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
FROM auth.users u
WHERE u.username = 'admin'
  AND NOT EXISTS (
      SELECT 1 FROM auth.user_credentials uc WHERE uc.user_id = u.id
  );

-- Update credential if admin already existed to ensure reset / valid password
UPDATE auth.user_credentials
SET password = '$2a$10$AXIUK5kXNVaJm9WiMSJMJ.OlnS8pS1lY5rjnqzbqPKcgKD4BKS9o6',
    failed_login_attempts = 0,
    locked_until = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = (SELECT id FROM auth.users WHERE username = 'admin');

-- 3. Assign SUPER_ADMIN and ADMIN roles to admin user in rbac.user_roles
INSERT INTO rbac.user_roles (
    id,
    user_id,
    role_id,
    created_at,
    updated_at,
    version
)
SELECT 
    gen_random_uuid(),
    u.id,
    r.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
FROM auth.users u
CROSS JOIN rbac.roles r
WHERE u.username = 'admin'
  AND r.code IN ('SUPER_ADMIN', 'ADMIN')
  AND NOT EXISTS (
      SELECT 1 FROM rbac.user_roles ur 
      WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- 4. Ensure all default permissions are assigned to SUPER_ADMIN and ADMIN roles
INSERT INTO rbac.role_permissions (
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    version
)
SELECT 
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
FROM rbac.roles r
CROSS JOIN rbac.permissions p
WHERE r.code IN ('SUPER_ADMIN', 'ADMIN')
  AND NOT EXISTS (
      SELECT 1 FROM rbac.role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
