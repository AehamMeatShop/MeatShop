-- First, insert default roles if they don't exist
INSERT INTO roles (name, created_at)
VALUES ('SUPER_ADMIN', CURRENT_TIMESTAMP),
       ('ADMIN', CURRENT_TIMESTAMP),
       ('MANAGER', CURRENT_TIMESTAMP),
       ('EMPLOYEE', CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;


ALTER TABLE employees
    ADD COLUMN role_id BIGINT;


UPDATE employees
SET role_id = (SELECT id FROM roles WHERE name = employees.role)
WHERE role IS NOT NULL;


ALTER TABLE employees
    ALTER COLUMN role_id SET NOT NULL;


ALTER TABLE employees
    DROP COLUMN role;


CREATE INDEX idx_employees_role_id ON employees (role_id);
