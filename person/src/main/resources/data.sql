DROP TABLE IF EXISTS person;

CREATE TABLE person
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    first_name       VARCHAR(250) NOT NULL,
    last_name        VARCHAR(250) NOT NULL,
    age              INT          NOT NULL,
    favorite_color   VARCHAR(50)  NOT NULL,
    create_timestamp TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_timestamp TIMESTAMP    ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO person (first_name, last_name, age, favorite_color, update_timestamp)
VALUES ('Siva', 'Balachandran', 35, 'Blue', CURRENT_TIMESTAMP);