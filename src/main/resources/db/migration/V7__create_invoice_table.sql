CREATE TYPE invoice_types AS ENUM( 'SELL','BUY');

CREATE TABLE invoices(
    id BIGSERIAL PRIMARY KEY ,
    invoice_type invoice_types NOT NULL ,
    party_id BIGINT NOT NULL ,
    notes VARCHAR(1023) NULL ,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_invoices_of_party FOREIGN KEY (party_id) REFERENCES parties(id)

);

CREATE INDEX idx_invoice_party_id ON invoices(party_id) ;
CREATE INDEX idx_created_at ON invoices(created_at);


