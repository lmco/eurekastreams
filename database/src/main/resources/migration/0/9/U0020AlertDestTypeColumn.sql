--
-- Set database version to 0.9.0020
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0020', 'U0020AlertDestTypeColumn.sql', 'Adds the destination type column on alert notifications.');

ALTER TABLE applicationalertnotification ADD COLUMN destinationtype character varying(255);
