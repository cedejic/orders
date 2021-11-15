INSERT INTO product (id, name, price)
VALUES (1, 'Coffee', 5.20);
INSERT INTO product (id, name, price)
VALUES (2, 'Juice', 3.75);
INSERT INTO product (id, name, price)
VALUES (3, 'Chocolate cake', 6.70);
INSERT INTO product (id, name, price)
VALUES (4, 'Lava cake', 8.35);

INSERT INTO purchase_order (id, customer_email, placed_at)
VALUES (1, 'john.doe@example.com', '2015-10-20 12:00:00');
INSERT INTO purchase_order (id, customer_email, placed_at)
VALUES (2, 'nell.goodwin@example.com', '2017-01-01 17:25:13');
INSERT INTO purchase_order (id, customer_email, placed_at)
VALUES (3, 'tyron.cullen@example.com', '2017-03-15 08:07:43');

INSERT INTO order_item (id, order_id, product_id, quantity, price)
VALUES (1, 1, 1, 1, 5.00);
INSERT INTO order_item (id, order_id, product_id, quantity, price)
VALUES (2, 1, 4, 1, 8.10);
INSERT INTO order_item (id, order_id, product_id, quantity, price)
VALUES (3, 2, 1, 1, 5.20);
INSERT INTO order_item (id, order_id, product_id, quantity, price)
VALUES (4, 2, 2, 2, 3.75);
INSERT INTO order_item (id, order_id, product_id, quantity, price)
VALUES (5, 3, 1, 1, 5.20);
INSERT INTO order_item (id, order_id, product_id, quantity, price)
VALUES (6, 3, 1, 1, 5.20);