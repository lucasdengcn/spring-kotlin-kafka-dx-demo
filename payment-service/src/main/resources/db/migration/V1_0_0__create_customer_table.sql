CREATE TABLE IF NOT EXISTS customer (
    id int NOT NULL PRIMARY KEY,
    name varchar(100),
    amount_available int,
    amount_reserved int
);

-- create sequence
CREATE SEQUENCE seq_customer_id INCREMENT 50 OWNED BY customer.id;

-- use sequence for the target column
ALTER TABLE customer ALTER COLUMN id SET DEFAULT nextval('seq_customer_id');