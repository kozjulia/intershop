CREATE TABLE items
(
    id          BIGINT       NOT NULL,
    title       VARCHAR(256) NOT NULL,
    description VARCHAR(1024),
    img_path    VARCHAR(1024),
    count       INTEGER,
    price       DECIMAL(10, 2),
    CONSTRAINT items_pk PRIMARY KEY (id)
);

CREATE SEQUENCE SEQ_ITEM
    START WITH 1 INCREMENT BY 1;