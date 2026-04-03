

CREATE TABLE invoices(
    id BIGSERIAL PRIMARY KEY ,
    invoice_type VARCHAR(255) NOT NULL ,
    party_id BIGINT NOT NULL ,
    notes VARCHAR(1023) NULL ,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP



);

CREATE INDEX idx_invoice_party_id ON invoices(party_id) ;
CREATE INDEX idx_created_at ON invoices(created_at);


