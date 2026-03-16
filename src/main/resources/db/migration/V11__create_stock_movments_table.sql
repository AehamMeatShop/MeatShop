CREATE TYPE stock_movment_types AS ENUM('PURCHASE','SELL','ADJUSTMENT','TRANSFER','TRANSFORM','WASTE','SHRINKAGE');

CREATE TABLE stock_movments (
   id BIGSERIAL PRIMARY KEY ,
   product_id BIGINT NOT NULL ,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   invoice_component_id BIGINT NULL ,
   quantity DECIMAL(12,3) NOT NULL ,
   stock_movment_type stock_movment_types NOT NULL ,
   notes varchar(1023) NULL ,

    CONSTRAINT fk_product_stock_movment FOREIGN KEY(product_id) REFERENCES products(id),
    CONSTRAINT fk_inv_component_stock_move FOREIGN KEY (invoice_component_id) REFERENCES invoice_components(id)
);

CREATE INDEX idx_stock_move_product_id ON stock_movments(product_id);
CREATE INDEX idx_invoice_components_id ON stock_movments(invoice_component_id)