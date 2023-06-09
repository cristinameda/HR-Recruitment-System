CREATE TABLE candidates
(
    candidate_id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name                varchar(50),
    phone_number        varchar(15),
    email               varchar(50),
    city                varchar(20),
    experience_years    integer,
    faculty             varchar(30),
    recruitment_channel varchar(30),
    birth_date          varchar(40),
    status              varchar(10) DEFAULT 'NO_STATUS'
);

CREATE TABLE positions
(
    position_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        varchar(50)
);

CREATE TABLE candidates_positions
(
    candidate_id bigint not null,
    position_id  bigint not null,
    FOREIGN KEY (candidate_id) REFERENCES candidates (candidate_id) ON DELETE CASCADE,
    FOREIGN KEY (position_id) REFERENCES positions (position_id) ON DELETE CASCADE,
    PRIMARY KEY (candidate_id, position_id)
);

CREATE TABLE files
(
    file_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(100),
    data         bytea,
    type         varchar(5),
    candidate_id bigint,
    FOREIGN KEY (candidate_id) REFERENCES candidates (candidate_id) ON DELETE CASCADE,
    PRIMARY KEY (file_id)

);

CREATE TABLE candidates_users
(
    candidate_id bigint not null,
    user_email varchar(320) not null,
    FOREIGN KEY (candidate_id) REFERENCES candidates (candidate_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS interviews
(
    interview_id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    candidate_id bigint NOT NULL,
    date_time varchar(50) NOT NULL,
    location varchar(100) NOT NULL,
    FOREIGN KEY (candidate_id) REFERENCES candidates (candidate_id) ON DELETE CASCADE
);

CREATE TABLE feedback
(
    comment      varchar(500),
    candidate_id bigint       not null,
    user_email   varchar(320) not null,
    status       bool,
    date_time    varchar(30),
    PRIMARY KEY (candidate_id, user_email),
    FOREIGN KEY (candidate_id) REFERENCES candidates (candidate_id) ON DELETE CASCADE
)
