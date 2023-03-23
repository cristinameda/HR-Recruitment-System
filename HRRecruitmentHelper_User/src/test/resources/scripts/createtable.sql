CREATE TABLE IF NOT EXISTS roles
(
    role_id bigint      NOT NULL AUTO_INCREMENT,
    name    varchar(22) NOT NULL UNIQUE,
    PRIMARY KEY (role_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   bigint      NOT NULL AUTO_INCREMENT,
    email     varchar(40) NOT NULL,
    password  varchar(100) NOT NULL,
    role_id   bigint,
    full_name varchar(40) NOT NULL,
    PRIMARY KEY (user_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id)
);