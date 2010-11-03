--
-- Set database version to 0.9.0040
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0041', 'U0041UpdateOAuth.sql', 'Remove unused oauthentrydto table.');

-- No need to explicitly drop constraints or sequence since they will be dropped automatically
DROP TABLE IF EXISTS oauthentrydto;
