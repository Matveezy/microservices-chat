CREATE TABLE users (
    id              BIGSERIAL   PRIMARY KEY,
    username        TEXT        NOT NULL UNIQUE,
    password        TEXT        NOT NULL,
    email           TEXT        NOT NULL UNIQUE,
    role            TEXT        NOT NULL
);

CREATE SEQUENCE user_id_seq START 4;

CREATE TABLE chats (
    id              BIGSERIAL   PRIMARY KEY,
    name            TEXT        NOT NULL UNIQUE,
    private         BOOLEAN     NOT NULL
);

CREATE SEQUENCE chat_id_seq;

CREATE TABLE chat_participants (
    user_id         BIGINT      NOT NULL,
    chat_id         BIGINT      NOT NULL,
    PRIMARY KEY (user_id, chat_id)
);

-- alter table chat_participants
-- drop constraint fk_chat_participant_user_id;
--
-- alter table chat_participants
-- drop constraint fk_chat_participant_chat_id;


CREATE TABLE messages (
    id              BIGSERIAL   PRIMARY KEY,
    body            TEXT        NOT NULL,
    creation_ts     TIMESTAMP   NOT NULL,
    sender_id       BIGINT      NOT NULL,
    chat_id         BIGINT      NOT NULL,
);

CREATE SEQUENCE message_id_seq;

CREATE TABLE message_deliveries (
    message_id      BIGINT      NOT NULL,
    receiver_id     BIGINT      NOT NULL,
    delivered       BOOLEAN     NOT NULL DEFAULT FALSE,
    delivery_ts     TIMESTAMP,
    PRIMARY KEY (message_id, receiver_id),
    CONSTRAINT fk_message_delivery_message_id
        FOREIGN KEY (message_id)
            REFERENCES messages (id),
    CONSTRAINT fk_message_delivery_receiver_id
        FOREIGN KEY (receiver_id)
            REFERENCES users (id)
);

INSERT INTO users (username, password, email, role) VALUES (
    'admin',
    '$2a$10$V.w48wGtPOA8CjLJdQ4ov./P10ni5VYysAhsnsMuqgcCAHAN9hzfy',
    'admin@mail.com',
    'ROLE_ADMIN'
);

INSERT INTO users (username, password, email, role) VALUES (
    'Mike',
    '$2a$10$BY5LieiHMxlYIJuYDi3/ne1XknKeE2kASR78w3wfP/06sYVs/wVwe',
    'mike@mail.com',
    'ROLE_USER'
);

INSERT INTO users (username, password, email, role) VALUES (
    'Johny',
    '$2a$10$BY5LieiHMxlYIJuYDi3/ne1XknKeE2kASR78w3wfP/06sYVs/wVwe',
    'johny@mail.com',
    'ROLE_ADMIN'
);