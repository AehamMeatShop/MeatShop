CREATE SEQUENCE IF NOT EXISTS authorities_seq
    START WITH 1
    INCREMENT BY 50;
CREATE TABLE authorities
(
    id         BIGSERIAL PRIMARY KEY,
    authority  VARCHAR(255) NOT NULL UNIQUE,

    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP

);

CREATE INDEX idx_authority_name ON authorities (authority);

