create table GSAK_OPPGAVE (
    id serial primary key,
    kontaktskjema_id number not null ,
    gsak_id number,
    gsak_status varchar(255),
);
