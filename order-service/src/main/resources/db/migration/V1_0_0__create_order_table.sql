-- create table
CREATE TABLE IF NOT EXISTS orders (
    id int NOT NULL PRIMARY KEY,
    customer_id int,
    product_id int,
    product_count int,
    price int,
    status text,
    source text
);

-- create sequence
CREATE SEQUENCE seq_orders_id INCREMENT 50 OWNED BY orders.id;

-- use sequence for the target column
ALTER TABLE orders ALTER COLUMN id SET DEFAULT nextval('seq_orders_id');