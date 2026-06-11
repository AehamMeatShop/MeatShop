CREATE SEQUENCE IF NOT EXISTS role_authorities_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE role_authorities
(
    id         BIGSERIAL PRIMARY KEY,
    role_id    BIGINT       NOT NULL,
    authority_id BIGINT      NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_authorities_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_role_authorities_authority FOREIGN KEY (authority_id) REFERENCES authorities (id) ON DELETE CASCADE,
    CONSTRAINT uk_role_authority UNIQUE (role_id, authority_id)
);

CREATE INDEX idx_role_authorities_role_id ON role_authorities (role_id);
CREATE INDEX idx_role_authorities_authority_id ON role_authorities (authority_id);
