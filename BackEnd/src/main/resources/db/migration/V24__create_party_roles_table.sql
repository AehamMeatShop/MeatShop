CREATE SEQUENCE IF NOT EXISTS party_roles_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE party_roles
(
    id         BIGSERIAL PRIMARY KEY,
    party_id   BIGINT       NOT NULL,
    party_type VARCHAR(100) NOT NULL,
    role_id    BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_party_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT uk_party_role UNIQUE (role_id, party_type, party_id)
);

CREATE INDEX idx_party_roles_role_id ON party_roles (role_id);
CREATE INDEX idx_party_roles_party_type ON party_roles (party_type);
CREATE INDEX idx_party_roles_party_id ON party_roles (party_id);
