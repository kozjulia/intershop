CREATE TABLE orders
(
    id BIGSERIAL NOT NULL,
    CONSTRAINT orders_pk PRIMARY KEY (id)
);

CREATE TABLE orders_items
(
    order_id BIGSERIAL NOT NULL,
    item_id  BIGSERIAL NOT NULL,
    count    INTEGER   NOT NULL,
    CONSTRAINT orders_items_pk PRIMARY KEY (order_id, item_id),
    CONSTRAINT fk_orders_items_to_orders FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_orders_items_to_items FOREIGN KEY (item_id) REFERENCES items (id)
);