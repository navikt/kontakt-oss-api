alter table KONTAKTSKJEMA add KOMMUNENR varchar(4);
update KONTAKTSKJEMA set KOMMUNENR='0000';
alter table KONTAKTSKJEMA modify KOMMUNENR varchar(4) not null;