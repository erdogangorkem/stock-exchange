CREATE TABLE stock_exchange
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL UNIQUE,
    description    VARCHAR(1024),
    live_in_market BOOLEAN      NOT NULL,
    version        INT          NOT NULL
);

CREATE TABLE stock
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255)   NOT NULL UNIQUE,
    description   VARCHAR(1024),
    current_price DECIMAL(15, 2) NOT NULL,
    last_update   TIMESTAMP      NOT NULL,
    version       INT            NOT NULL
);

CREATE TABLE stock_exchange_stock
(
    stock_exchange_id BIGINT NOT NULL,
    stock_id          BIGINT NOT NULL,
    PRIMARY KEY (stock_exchange_id, stock_id),
    FOREIGN KEY (stock_exchange_id) REFERENCES stock_exchange (id),
    FOREIGN KEY (stock_id) REFERENCES stock (id)
);
