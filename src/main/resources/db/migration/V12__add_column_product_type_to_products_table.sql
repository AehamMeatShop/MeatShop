CREATE TYPE product_types AS ENUM( 'SIMPLE','COMPOSITE','SERVICE');

ALTER TABLE products ADD COLUMN product_type product_types NOT NULL ;