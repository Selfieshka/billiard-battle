CREATE SEQUENCE player_sequence
    START WITH 100000
    INCREMENT BY 1
    CACHE 50;

CREATE TABLE player
(
    player_id BIGINT       NOT NULL DEFAULT NEXTVAL('player_sequence'),
    name      VARCHAR(50)  NOT NULL,
    password  VARCHAR(255) NOT NULL,
    money     INT          NOT NULL DEFAULT 1000,
    ------------------------------
    CONSTRAINT player_id PRIMARY KEY (player_id),
    CONSTRAINT name_uq UNIQUE (name)
);
