CREATE TABLE `ORDER_TABLE`
(
    ID              BIGINT PRIMARY KEY AUTO_INCREMENT,
    USER_ID         BIGINT,
    TOTAL_PRICE     DECIMAL,      -- 价格，单位分
    ADDRESS         VARCHAR(1024),
    EXPRESS_COMPANY VARCHAR(16),
    EXPRESS_ID      VARCHAR(128),
    STATUS          VARCHAR(16), -- PENDING 待付款 PAID 已付款 DELIVERED 物流中 RECEIVED 已收货
    CREATED_AT      TIMESTAMP NOT NULL DEFAULT NOW(),
    UPDATED_AT      TIMESTAMP NOT NULL DEFAULT NOW()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE `ORDER_GOODS`
(
    ID       BIGINT PRIMARY KEY AUTO_INCREMENT,
    GOODS_ID BIGINT,
    ORDER_ID BIGINT,
    NUMBER   BIGINT -- 单位 分
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `ORDER_TABLE` (ID, USER_ID, TOTAL_PRICE, ADDRESS, EXPRESS_COMPANY, EXPRESS_ID, STATUS)
VALUES (1, 1, 1400, '火星', '顺丰', '运单1234567', 'delivered');

INSERT INTO ORDER_GOODS(GOODS_ID, ORDER_ID, NUMBER)
VALUES (1, 1, 5),
       (2, 1, 9);