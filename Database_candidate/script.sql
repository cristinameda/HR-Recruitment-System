CREATE TABLE IF NOT EXISTS candidates
(
    candidate_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    name character varying(50),
    phone_number character varying(15),
    email character varying(320) UNIQUE,
    city character varying(20),
    experience_years integer,
    faculty character varying(30),
    recruitment_channel character varying(30),
    birth_date character varying(50),
    status character varying(10) DEFAULT 'NO_STATUS',
    CONSTRAINT pk_candidates PRIMARY KEY (candidate_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS candidates
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS positions
(
    position_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    name character varying(50),
    CONSTRAINT pk_positions PRIMARY KEY (position_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS positions
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS candidates_positions
(
    candidate_id bigint NOT NULL,
    position_id bigint NOT NULL,
    CONSTRAINT pk_candidates_positions PRIMARY KEY (candidate_id, position_id),
    CONSTRAINT fk_candidates_positions_ref_candidates FOREIGN KEY (candidate_id)
        REFERENCES candidates (candidate_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_candidates_positions_ref_positions FOREIGN KEY (position_id)
        REFERENCES positions (position_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS candidates_positions
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS feedback
(
    comment character varying(500),
    candidate_id bigint NOT NULL,
	user_email character varying(320) NOT NULL,
	status boolean,
    date_time character varying(30),
    CONSTRAINT pk_comments PRIMARY KEY (candidate_id,user_email),
    CONSTRAINT fk_comments_ref_candidates FOREIGN KEY (candidate_id)
        REFERENCES candidates (candidate_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS feedback
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.candidates_users
(
    candidate_id bigint NOT NULL,
    user_email character varying(320) NOT NULL,
    CONSTRAINT fk_candidates_users_ref_candidates FOREIGN KEY (candidate_id)
        REFERENCES public.candidates (candidate_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.candidates_users
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS files
(
    file_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    name character varying(100),
    data bytea,
    type character varying(5),
    candidate_id bigint,
    CONSTRAINT pk_files PRIMARY KEY (file_id),
    CONSTRAINT fk_files_ref_candidates FOREIGN KEY (candidate_id)
        REFERENCES candidates (candidate_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS files
    OWNER to postgres;


insert into positions(name) values ('Back-end developer');
insert into positions(name) values ('Cloud/software architect');
insert into positions(name) values ('Cloud system engineer');
insert into positions(name) values ('Front-end developer');
insert into positions(name) values ('Full-stack developer');
insert into positions(name) values ('Java developer');
insert into positions(name) values ('Software quality assurance analyst');
insert into positions(name) values ('SWE');

CREATE TABLE IF NOT EXISTS interviews
(
    interview_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    candidate_id bigint NOT NULL,
    date_time character varying(50) NOT NULL,
    location character varying(100) NOT NULL,
    CONSTRAINT interviews_pkey PRIMARY KEY (interview_id),
    CONSTRAINT fk_candidate_id_ref_candidates FOREIGN KEY (candidate_id)
        REFERENCES candidates (candidate_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS interviews
    OWNER to postgres;


