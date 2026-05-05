CREATE TABLE product_components (
    id BIGSERIAL PRIMARY KEY ,
    ratio_in_kg DECIMAL(5 , 4) CHECK ( ratio_in_kg > 0 AND ratio_in_kg <=1 ),
    product_id BIGINT NOT NULL ,
    component_id BIGINT NOT NULL ,
    CONSTRAINT fk_product_product_components FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE ,
    CONSTRAINT fk_product_product_included FOREIGN KEY (component_id) REFERENCES products(id) ON DELETE CASCADE ,
    CONSTRAINT chk_not_products_components_self CHECK ( product_id != component_id ) ,
    CONSTRAINT unique_product_id_comp_id UNIQUE (product_id,component_id)
);

CREATE INDEX idx_product_id ON product_components(product_id) ;
CREATE INDEX idx_component_id ON product_components(component_id)

