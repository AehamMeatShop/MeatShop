CREATE TABLE products(
     id BIGSERIAL PRIMARY KEY ,
     product_name VARCHAR(255) NOT NULL UNIQUE,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     description VARCHAR(1023),
     category_id BIGINT ,
     CONSTRAINT fk_categories_product FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX idx_category_id ON products(category_id);
CREATE INDEX idx_prod_description ON products(description)

