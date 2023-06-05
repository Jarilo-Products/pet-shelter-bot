-- liquibase formatted sql

-- changeset safgbad:1
CREATE TABLE pets
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type          VARCHAR NOT NULL,
    name          VARCHAR NOT NULL,
    birthdate     DATE    NOT NULL,
    status        VARCHAR NOT NULL DEFAULT 'OWNERLESS',
    health_status VARCHAR NOT NULL DEFAULT 'HEALTHY',
    sex           VARCHAR NOT NULL DEFAULT 'MALE'
);
