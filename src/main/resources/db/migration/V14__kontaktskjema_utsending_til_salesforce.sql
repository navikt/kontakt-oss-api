create table KONTAKTSKJEMA_UTSENDING (
    id serial primary key,
    kontaktskjema_id numeric not null,
    utsending_status varchar(255),
    opprettet timestamp(6) default current_timestamp
);
