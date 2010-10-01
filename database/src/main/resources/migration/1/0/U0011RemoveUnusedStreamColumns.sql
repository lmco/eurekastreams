--
-- Set database version to 1.0.0011
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0011', 'U0011RemoveUnusedStreamColumns.sql', 'Remove unused stream columns');

ALTER TABLE Person DROP COLUMN streamviewhiddenlineindex;
