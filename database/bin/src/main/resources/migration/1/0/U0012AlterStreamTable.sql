--
-- Set database version to 1.0.0012
--
insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0012', 'U0005AlterStreamTable.sql', 'Alter the Stream table.');

alter table Person_Stream drop constraint person_stream_pkey;
alter table person_stream add constraint person_stream_pkey primary key (personid, streamid);
