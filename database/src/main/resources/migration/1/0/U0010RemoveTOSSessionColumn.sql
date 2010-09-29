--
-- Set database version to 1.0.0010
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0010', 'U0010RemoveTOSSessionColumn.sql', 'Remove unused column for TOS per-session option');

ALTER TABLE systemsettings DROP COLUMN istosdisplayedeverysession;