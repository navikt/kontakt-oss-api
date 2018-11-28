drop table if exists KONTAKTSKJEMA;

create table KONTAKTSKJEMA (
	id serial primary key,
	opprettet timestamp default current_timestamp,
	melding varchar(10000) not null,
	fylke varchar(255) not null,
  kommune varchar(255) not null,
  bedriftsnavn varchar(255) not null,
  fornavn varchar(255) not null,
  etternavn varchar(255) not null,
  epost varchar(255) not null,
  telefonnr varchar(255) not null
);