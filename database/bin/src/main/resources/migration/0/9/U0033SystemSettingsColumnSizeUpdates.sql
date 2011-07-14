--
-- Set database version to 0.9.0033
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0033', 'U0033SystemSettingsColumnSizeUpdates', 'Update TOS and plugin config message columns to be text.');

ALTER TABLE systemsettings 
	ALTER COLUMN pluginwarning TYPE text,
	ALTER COLUMN termsofservice TYPE text;
