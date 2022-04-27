create table KONTAKTSKJEMA (
    id serial primary key,
	opprettet timestamp not null default current_timestamp,
    fylkesenhetsnr varchar(255) not null,
    kommune varchar(255) not null,
    kommunenr VARCHAR(6),
    bedriftsnavn varchar(255) not null,
    navn varchar(255),
    epost varchar(255) not null,
    telefonnr varchar(255) not null,
    orgnr varchar(30),
    tema_type varchar(100),
    tema varchar(255),
    har_snakket_med_ansattrepresentant BOOLEAN
);


create table KONTAKTSKJEMA_UTSENDING (
    id serial primary key,
    kontaktskjema_id numeric not null,
    utsending_status varchar(255),
    opprettet timestamp(6) default current_timestamp
);


CREATE TABLE SHEDLOCK (
    name VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),
    PRIMARY KEY (name)
);

CREATE TABLE FYLKESINNDELING (
    id BIGSERIAL PRIMARY KEY,
    kommune_bydel varchar(50000) not null,
    last_updated timestamp not null default current_timestamp
);
insert into FYLKESINNDELING (kommune_bydel) values ('Test');