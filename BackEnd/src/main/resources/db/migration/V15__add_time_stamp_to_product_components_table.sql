ALTER TABLE product_components add column created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE product_components add column updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;