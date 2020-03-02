alter table kontaktskjema add fylkesenhetsnr varchar(255);
update kontaktskjema set fylkesenhetsnr = fylke;
alter table kontaktskjema alter column fylkesenhetsnr set not null;