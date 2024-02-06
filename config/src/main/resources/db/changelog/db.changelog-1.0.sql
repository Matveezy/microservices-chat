--liquibase formatted sql

--changeset admin:1
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email    TEXT NOT NULL UNIQUE,
    role     TEXT NOT NULL
);

CREATE TABLE chats
(
    id      BIGSERIAL PRIMARY KEY,
    name    TEXT    NOT NULL UNIQUE,
    private BOOLEAN NOT NULL
);


CREATE TABLE chat_participants
(
    user_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, chat_id)
);

CREATE TABLE messages
(
    id          BIGSERIAL PRIMARY KEY,
    body        TEXT      NOT NULL,
    creation_ts TIMESTAMP NOT NULL,
    sender_id   BIGINT    NOT NULL,
    chat_id     BIGINT    NOT NULL,
);

CREATE TABLE message_deliveries
(
    message_id  BIGINT  NOT NULL,
    receiver_id BIGINT  NOT NULL,
    delivered   BOOLEAN NOT NULL DEFAULT FALSE,
    delivery_ts TIMESTAMP,
    PRIMARY KEY (message_id, receiver_id)
);
