--
-- Set database version to 0.9.0026
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0026', 'U0026ShrinkSystemSettingsSupportColumns.sql', 'Reduces supportemailaddress and supportphonenumber columns to 50 characters from 255 characters.');

UPDATE systemsettings SET supportemailaddress = substr(supportemailaddress, 1, 50);
UPDATE systemsettings SET supportphonenumber = substr(supportphonenumber, 1, 50);

ALTER TABLE systemsettings ALTER COLUMN supportemailaddress TYPE varchar(50);
ALTER TABLE systemsettings ALTER COLUMN supportphonenumber TYPE varchar(50); 
