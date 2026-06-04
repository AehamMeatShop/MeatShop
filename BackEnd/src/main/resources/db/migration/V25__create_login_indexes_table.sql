CREATE SEQUENCE IF NOT EXISTS login_indexes_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE login_indexes (
    id BIGINT PRIMARY KEY DEFAULT nextval('login_indexes_seq'),

    subject_id BIGINT NOT NULL,

    subject_type VARCHAR(50) NOT NULL,

    email VARCHAR(255) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_login_indexes_email UNIQUE (email),
    CONSTRAINT uk_login_indexes_subject UNIQUE (subject_type, subject_id)
);
