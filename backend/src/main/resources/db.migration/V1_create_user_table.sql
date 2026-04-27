CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,

                       email VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       hashed_password VARCHAR(255) NOT NULL,

                       created_at TIMESTAMP NOT NULL DEFAULT now(),

                       CONSTRAINT users_email_unique UNIQUE (email),
                       CONSTRAINT users_email_not_blank CHECK (length(trim(email)) > 0),
                       CONSTRAINT users_first_name_not_blank CHECK (length(trim(first_name)) > 0),
                       CONSTRAINT users_last_name_not_blank CHECK (length(trim(last_name)) > 0),
                       CONSTRAINT users_password_not_blank CHECK (length(trim(hashedPassword)) > 0)
);