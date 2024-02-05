--liquibase formatted sql

--changeset matthew:1
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email    TEXT NOT NULL UNIQUE,
    role     TEXT NOT NULL
);