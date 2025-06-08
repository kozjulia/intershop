CREATE TABLE orders
(
    id BIGINT NOT NULL,
    CONSTRAINT orders_pk PRIMARY KEY (id)
);

CREATE TABLE orders_items
(
    order_id BIGINT NOT NULL,
    item_id  BIGINT NOT NULL,
    CONSTRAINT orders_items_pk PRIMARY KEY (order_id, item_id),
    CONSTRAINT fk_orders_items_to_orders FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_orders_items_to_items FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE SEQUENCE SEQ_ORDER
    START WITH 1 INCREMENT BY 1;