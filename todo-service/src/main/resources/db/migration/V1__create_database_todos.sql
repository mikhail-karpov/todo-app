CREATE TABLE todos(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE
);