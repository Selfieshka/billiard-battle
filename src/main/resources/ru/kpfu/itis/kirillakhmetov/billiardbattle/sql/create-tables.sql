CREATE TABLE player
(
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    name     VARCHAR(50)           NOT NULL,
    password VARCHAR(50)           NOT NULL,
    money    INT                   NOT NULL
);
