-- First, insert default roles if they don't exist
INSERT INTO roles (name, created_at)
VALUES ('SUPER_ADMIN', CURRENT_TIMESTAMP),
       ('ADMIN', CURRENT_TIMESTAMP),
       ('MANAGER', CURRENT_TIMESTAMP),
       ('EMPLOYEE', CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;



