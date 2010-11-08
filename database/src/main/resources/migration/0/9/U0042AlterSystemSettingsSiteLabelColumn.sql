--
-- Set database version to 0.9.0042
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0042', 'U0042AlterSystemSettingsSiteLabelColumn.sql', 'Increases the size of the site label column to a text field from varchar 255');

ALTER TABLE systemsettings ALTER COLUMN sitelabel TYPE text; 