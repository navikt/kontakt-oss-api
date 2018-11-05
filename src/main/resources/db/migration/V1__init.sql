create table INTERESSEMELDING (
	id serial primary key,
	fylke varchar(255) not null,
	kommune varchar(255) not null,
	bedriftsnavn varchar(255) not null,
	fornavn varchar(255) not null,
	etternavn varchar(255) not null,
	epost varchar(255) not null,
);

insert into INTERESSEMELDING (fylke, kommune, bedriftsnavn, fornavn, etternavn, epost)
values ('Hedmark', 'Elverum', 'Flesk', 'Heidi', 'Olavsen', 'hei@nav.no');