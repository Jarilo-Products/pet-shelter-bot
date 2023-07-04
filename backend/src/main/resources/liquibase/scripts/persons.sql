-- liquibase formatted sql

-- changeset safgbad:1
CREATE TABLE persons
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    chat_id       BIGINT  NOT NULL,
    first_name    VARCHAR NOT NULL,
    last_name     VARCHAR NOT NULL,
    middle_name   VARCHAR,
    birthdate     DATE    NOT NULL,
    phone         VARCHAR,
    email         VARCHAR,
    address       VARCHAR,
    pet_id        BIGINT REFERENCES pets (id),
    probation_end DATE
);

-- changeset evgeniy:2
ALTER TABLE persons
    ADD COLUMN last_report_date DATE;

-- changeset safgbad:3
ALTER TABLE persons
    ADD COLUMN is_volunteer BOOLEAN NOT NULL DEFAULT FALSE;

-- changeset lolipis:4
ALTER TABLE persons
DROP CONSTRAINT persons_pkey;

-- changeset lolipis:5
ALTER TABLE persons
    ADD CONSTRAINT persons_pkey PRIMARY KEY (chat_id);

-- changeset lolipis:6
ALTER TABLE persons
    DROP COLUMN id;
