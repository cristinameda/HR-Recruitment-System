CREATE TABLE IF NOT EXISTS public.roles
(
    role_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    name character varying(30) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (role_id),
	CONSTRAINT unique_name UNIQUE (name)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.roles
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.users
(
    user_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    email character varying(320) NOT NULL,
    password character varying(64)  NOT NULL,
    role_id bigint,
    full_name character varying(40) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT unique_email UNIQUE (email),
    CONSTRAINT fk_users_roles FOREIGN KEY (role_id)
        REFERENCES public.roles (role_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;

INSERT INTO roles(name) VALUES ('Admin'); 
INSERT INTO roles(name) VALUES ('HrRepresentative'); 
INSERT INTO roles(name) VALUES ('TechnicalInterviewer'); 
INSERT INTO roles(name) VALUES ('PTE'); 

INSERT INTO users(email, password, role_id, full_name) VALUES ('ADMIN@yahoo.com', 'b982b70ab5106c703497b73af8b723e7cfbf00b87b9955ed05fb81a34f97df77', 1, 'Mark Szabo');

INSERT INTO users(email, password, role_id, full_name) VALUES ('HR@yahoo.com', 'b982b70ab5106c703497b73af8b723e7cfbf00b87b9955ed05fb81a34f97df77', 2, 'Monica Popa');

INSERT INTO users(email, password, role_id, full_name) VALUES ('TechInterviewer@yahoo.com', 'b982b70ab5106c703497b73af8b723e7cfbf00b87b9955ed05fb81a34f97df77', 3, 'Alina Orbeanu');

INSERT INTO users(email, password, role_id, full_name) VALUES ('PTE@yahoo.com', 'b982b70ab5106c703497b73af8b723e7cfbf00b87b9955ed05fb81a34f97df77', 4, 'Meda Titu');
