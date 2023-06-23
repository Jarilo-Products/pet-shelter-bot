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
-- changeset lolipis:2
ALTER TABLE reports
DROP CONSTRAINT reports_person_id_fkey;

-- changeset lolipis:3
ALTER TABLE reports
    RENAME COLUMN person_id TO chat_id;

-- changeset lolipis:4
ALTER TABLE reports
    RENAME COLUMN image_path TO file_id;

-- changeset lolipis:5
ALTER TABLE reports
    ADD CONSTRAINT fk_persons_report FOREIGN KEY (chat_id) REFERENCES persons (chat_id);

-- changeset lolipis:6
ALTER TABLE reports
    ALTER COLUMN file_id DROP NOT NULL;

-- changeset lolipis:7
ALTER TABLE reports
    ALTER COLUMN text DROP NOT NULL;
