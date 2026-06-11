CREATE SEQUENCE IF NOT EXISTS party_authorities_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE party_authorities
(
    id           BIGSERIAL PRIMARY KEY,
    authority_id BIGINT       NOT NULL,
    party_type   VARCHAR(100) NOT NULL,
    party_id     BIGINT       NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_party_authorities_authority FOREIGN KEY (authority_id) REFERENCES authorities (id) ON DELETE CASCADE,
    CONSTRAINT uk_party_authority UNIQUE (authority_id, party_type, party_id)
);

CREATE INDEX idx_party_authorities_authority_id ON party_authorities (authority_id);
CREATE INDEX idx_party_authorities_party_type ON party_authorities (party_type);
CREATE INDEX idx_party_authorities_party_id ON party_authorities (party_id);
