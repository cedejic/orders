DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS purchase_order;

CREATE TABLE product
(
    id    INT PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(150)   NOT NULL,
    price DECIMAL(20, 2) NOT NULL,
    UNIQUE KEY unique_product_name (name)
);

CREATE TABLE purchase_order
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    customer_email VARCHAR(150) NOT NULL,
    placed_at     TIMESTAMP
);

CREATE TABLE order_item
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    order_id   INT            NOT NULL,
    product_id INT            NOT NULL,
    quantity   INT            NOT NULL,
    price      DECIMAL(20, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES purchase_order (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);
