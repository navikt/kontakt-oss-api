create table GSAK_OPPGAVE (
    id serial primary key,
    kontaktskjema_id numeric not null,
    gsak_id numeric,
    status varchar(255),
    opprettet timestamp(6) default current_timestamp
);
