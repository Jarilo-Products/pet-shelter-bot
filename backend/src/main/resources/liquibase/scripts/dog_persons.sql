-- liquibase formatted sql

-- changeset safgbad:1
CREATE TABLE dog_persons
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    chat_id      BIGINT    NOT NULL,
    first_name   VARCHAR   NOT NULL,
    last_name    VARCHAR   NOT NULL,
    middle_name  VARCHAR,
    birthdate    TIMESTAMP NOT NULL,
    phone        VARCHAR,
    email        VARCHAR,
    address      VARCHAR,
    last_command VARCHAR
);

-- changeset safgbad:2
ALTER TABLE dog_persons DROP COLUMN last_command;
