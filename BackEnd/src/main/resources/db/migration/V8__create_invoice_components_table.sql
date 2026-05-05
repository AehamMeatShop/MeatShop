CREATE TABLE invoice_components (
   id BIGSERIAL PRIMARY KEY ,

   product_id BIGINT NOT NULL ,

   quantity_kg DECIMAL(12,3) NOT NULL CHECK ( quantity_kg > 0 ),

   price_kg DECIMAL(12,3) NOT NULL CHECK ( price_kg> 0 ),

   invoice_id BIGINT NOT NULL ,

    CONSTRAINT fk_invoice_components FOREIGN KEY (invoice_id) REFERENCES invoices(id)



);

CREATE INDEX idx_invoice_id ON invoice_components(invoice_id) ;
CREATE INDEX idx_component_product_id ON invoice_components(product_id)





