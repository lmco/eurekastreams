insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0042', 'U0040DropPersonColumns', 'Drop unused person columns.');

alter table person drop column biography;
alter table person drop column location;

