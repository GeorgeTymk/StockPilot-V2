CREATE TABLE sale_items (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL NOT NULL,
    price DECIMAL NOT NULL,

    FOREIGN KEY (sale_id)
        REFERENCES sales(id)
        ON DELETE CASCADE,

    FOREIGN KEY (product_id)
        REFERENCES products(id)
);
