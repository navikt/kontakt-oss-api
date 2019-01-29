create table GSAK_OPPGAVE (
    id serial primary key,
    kontaktskjema_id number not null ,
    gsak_id number,
    status varchar(255),
    opprettet timestamp(6) default current_timestamp
);
