--
-- Set database version to 1.0.0015
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0015', 'U0015AlterSystemSettingsSiteLabelColumn.sql', 'Increases the size of the site label column to a text field from varchar 255');

ALTER TABLE systemsettings ALTER COLUMN sitelabel TYPE text; 