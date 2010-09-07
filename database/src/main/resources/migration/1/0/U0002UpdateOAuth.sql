--
-- Set database version to 1.0.0002
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0002', 'U0002UpdateOAuth.sql', 'Remove unused oauthentrydto table.');

-- No need to explicitly drop constraints or sequence since they will be dropped automatically
DROP TABLE IF EXISTS oauthentrydto;
