CREATE SEQUENCE IF NOT EXISTS authorities_seq
    START WITH 1
    INCREMENT BY 50;
CREATE TABLE authorities
(
    id         BIGSERIAL PRIMARY KEY,
    authority  VARCHAR(255) NOT NULL UNIQUE,
    role_id    BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_authorities_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE INDEX idx_authority_name ON authorities (authority);
CREATE INDEX idx_authorities_role_id ON authorities (role_id);
