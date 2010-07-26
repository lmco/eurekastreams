--
-- Set database version to 0.9.0015
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0015', 'U0015AddActivityFlaggedColumn.sql', 'Adds the flagged as inappropriate column on activities.');

ALTER TABLE activity ADD COLUMN flagged boolean NOT NULL DEFAULT false;
