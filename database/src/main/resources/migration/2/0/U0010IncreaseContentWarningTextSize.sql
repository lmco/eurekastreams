insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0010', 'U0010IncreaseContentWarningTextSize.sql', 'Increase SystemSettings.contentWarningText size');

ALTER TABLE SystemSettings ALTER COLUMN contentWarningText TYPE varchar(2000);