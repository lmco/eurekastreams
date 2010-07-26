--
-- Set database version to 0.9.0019
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0019', 'U0019RenameAlertColumns.sql', 'Rename two columns in ApplicationAlertNotification table.');

ALTER TABLE applicationalertnotification RENAME COLUMN groupname to destinationname;
ALTER TABLE applicationalertnotification RENAME COLUMN groupshortname to destinationshortname;
