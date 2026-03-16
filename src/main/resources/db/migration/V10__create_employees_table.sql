CREATE TYPE employee_role AS ENUM (
    'SUPER_ADMIN' ,
    'MANAGER',
    'BUTCHER',
    'WORKER',
    'CASHIER'
    );

CREATE TYPE employee_status AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'SUSPENDED'
    );

CREATE TABLE employees (
      id BIGSERIAL PRIMARY KEY,

      salary BIGINT NOT NULL CHECK (salary >= 0),

      party_id BIGINT NOT NULL,

      role employee_role NOT NULL,

      status employee_status NOT NULL,

      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      updated_at TIMESTAMP,

      CONSTRAINT fk_employee_party  FOREIGN KEY (party_id) REFERENCES parties(id) ON DELETE RESTRICT
);

CREATE INDEX idx_employee_party_id ON employees(party_id);

CREATE INDEX idx_employee_status ON employees(status);
