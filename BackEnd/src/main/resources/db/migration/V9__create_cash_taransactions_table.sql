

CREATE TABLE cash_transactions (
    id BIGSERIAL PRIMARY KEY ,
    type VARCHAR(255) NOT NULL ,
    party_id BIGINT NOT NULL ,
    notes varchar(1023) ,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    ammount BIGINT NOT NULL CHECK ( ammount > 0 ) ,

    invoice_id BIGINT ,


    CONSTRAINT fk_invoice_cash_transaction FOREIGN KEY (invoice_id) REFERENCES invoices(id)

);

CREATE INDEX idx_cash_trans_party_id ON cash_transactions(party_id);
CREATE INDEX idx_cash_trans_invoice_id ON cash_transactions(invoice_id)
