--liquibase formatted sql

--changeset admin:1
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

INSERT INTO chats
    (id, name, private)
VALUES (1, 'First Group Chat', false),
       (2, 'Second Group Chat', false),
       (3, 'First Private Chat', true),
       (4, 'Second Private Chat', true);

SELECT SETVAL('chats_id_seq', (SELECT MAX(id) from chats));

INSERT INTO chat_participants
    (user_id, chat_id)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (1, 2),
       (2, 2),
       (4, 3),
       (5, 3),
       (1, 4),
       (2, 4);
