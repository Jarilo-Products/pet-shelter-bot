-- liquibase formatted sql

-- changeset safgbad:1
CREATE TABLE cats
(
    id        BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name      VARCHAR   NOT NULL,
    birthdate TIMESTAMP NOT NULL
);
