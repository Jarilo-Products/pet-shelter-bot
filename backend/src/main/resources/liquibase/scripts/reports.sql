-- liquibase formatted sql

-- changeset safgbad:1
CREATE TABLE reports
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    pet_id     BIGINT  NOT NULL REFERENCES pets (id),
    person_id  BIGINT  NOT NULL REFERENCES persons (id),
    image_path VARCHAR NOT NULL,
    text       VARCHAR NOT NULL,
    date       DATE    NOT NULL
);
