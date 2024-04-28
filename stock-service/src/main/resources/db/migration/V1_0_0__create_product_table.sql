CREATE TABLE IF NOT EXISTS product (
    id int NOT NULL PRIMARY KEY,
    name text,
    available_items int,
    reserved_items int
);

-- create sequence
CREATE SEQUENCE seq_product_id INCREMENT 50 OWNED BY product.id;

-- use sequence for the target column
ALTER TABLE product ALTER COLUMN id SET DEFAULT nextval('seq_product_id');