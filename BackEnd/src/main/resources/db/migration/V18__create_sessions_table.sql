CREATE SEQUENCE IF NOT EXISTS sessions_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE sessions (
      id BIGINT PRIMARY KEY DEFAULT nextval('sessions_seq'),

      refresh_token_hash VARCHAR(512) NOT NULL,

      party_type VARCHAR(50) NOT NULL,
      party_id BIGINT NOT NULL,

      state VARCHAR(30) NOT NULL,

      device_id VARCHAR(255) NOT NULL,

      baseline_fingerprint VARCHAR(512) NOT NULL,
      last_fingerprint VARCHAR(512) NOT NULL,

      trust_score INTEGER NOT NULL DEFAULT 100
          CHECK (trust_score >= 0 AND trust_score <= 100),

      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      last_seen_at TIMESTAMP,

      revoked_at TIMESTAMP
);