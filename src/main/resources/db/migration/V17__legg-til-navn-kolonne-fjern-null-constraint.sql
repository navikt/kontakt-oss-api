alter table kontaktskjema add navn varchar(255);
alter table kontaktskjema alter column etternavn drop not null;
alter table kontaktskjema alter column fornavn drop not null;