CREATE TABLE employees
(
    id         BIGSERIAL PRIMARY KEY,

    salary     BIGINT       NOT NULL CHECK (salary >= 0),

    party_id   BIGINT       NOT NULL,


    status     varchar(255) NOT NULL,

    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP


);

CREATE INDEX idx_employee_party_id ON employees (party_id);

CREATE INDEX idx_employee_status ON employees (status);
