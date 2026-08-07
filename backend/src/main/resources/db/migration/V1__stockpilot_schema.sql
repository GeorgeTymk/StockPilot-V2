CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);


CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    quantity DECIMAL NOT NULL,
    price DECIMAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    quantity DECIMAL NOT NULL,
    unit VARCHAR(50) NOT NULL,
    minimum_stock DECIMAL NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE inventory_history (
    id BIGSERIAL PRIMARY KEY,
    ingredient_id BIGINT NOT NULL,
    movement_type VARCHAR(100) NOT NULL,
    quantity DECIMAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_inventory_ingredient
    FOREIGN KEY (ingredient_id)
    REFERENCES ingredients(id)
);


CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    message TEXT,
    type VARCHAR(100),
    activity_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE supplier_ingredients (
    id BIGSERIAL PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,

    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(id)
    ON DELETE CASCADE,

    FOREIGN KEY (ingredient_id)
    REFERENCES ingredients(id)
    ON DELETE CASCADE
);


CREATE TABLE purchases (
    id BIGSERIAL PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    total_cost DECIMAL DEFAULT 0,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(id)
);


CREATE TABLE purchase_items (
    id BIGSERIAL PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity DECIMAL NOT NULL,
    cost DECIMAL NOT NULL,

    FOREIGN KEY (purchase_id)
    REFERENCES purchases(id)
    ON DELETE CASCADE,

    FOREIGN KEY (ingredient_id)
    REFERENCES ingredients(id)
);


CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    selling_price DECIMAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE recipe_ingredients (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity_used DECIMAL NOT NULL,

    FOREIGN KEY (recipe_id)
    REFERENCES recipes(id)
    ON DELETE CASCADE,

    FOREIGN KEY (ingredient_id)
    REFERENCES ingredients(id)
    ON DELETE CASCADE
);


CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    quantity INTEGER DEFAULT 1,
    total DECIMAL DEFAULT 0,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (recipe_id)
    REFERENCES recipes(id)
    ON DELETE CASCADE
);


CREATE INDEX idx_ingredient_name
ON ingredients(name);


CREATE INDEX idx_product_name
ON products(name);


CREATE INDEX idx_sales_date
ON sales(sale_date);