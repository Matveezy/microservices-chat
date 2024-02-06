--liquibase formatted sql

--changeset admin:1
CREATE TABLE messages (
                          id              BIGSERIAL   PRIMARY KEY,
                          body            TEXT        NOT NULL,
                          creation_ts     TIMESTAMP,
                          sender_id       BIGINT      NOT NULL,
                          chat_id         BIGINT      NOT NULL
);


CREATE TABLE message_deliveries (
                                    message_id      BIGINT      NOT NULL,
                                    receiver_id     BIGINT      NOT NULL,
                                    delivered       BOOLEAN     NOT NULL DEFAULT FALSE,
                                    delivery_ts     TIMESTAMP
);