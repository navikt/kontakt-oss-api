create table KONTAKTSKJEMA (
	id serial primary key,
	fylke varchar(255) not null,
  kommune varchar(255) not null,
  bedriftsnavn varchar(255) not null,
  fornavn varchar(255) not null,
  etternavn varchar(255) not null,
  epost varchar(255) not null,
  telefonnr varchar(255) not null,
);


insert into KONTAKTSKJEMA (fylke, kommune, bedriftsnavn, fornavn, etternavn, epost, telefonnr)
values ('Østfold', 'Moss', 'Flesk og ris AS', 'Nora', 'Østervest', 'søskjer@fleskogris.no', '+47 12 34 56 78');