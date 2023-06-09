-- liquibase formatted sql

-- changeset safgbad:1
CREATE TABLE last_commands
(
    chat_id      BIGINT PRIMARY KEY,
    last_command VARCHAR,
    is_closed    BOOLEAN
);

-- changeset safgbad:2
ALTER TABLE last_commands
    ADD COLUMN active_pet VARCHAR;

-- changeset safgbad:3
ALTER TABLE last_commands
    DROP COLUMN active_pet,
    ADD COLUMN active_type VARCHAR;

-- changeset safgbad:4
ALTER TABLE last_commands
    ADD COLUMN last_message_id INTEGER;
