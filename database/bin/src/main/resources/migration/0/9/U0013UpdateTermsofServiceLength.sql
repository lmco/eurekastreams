--
-- Set database version to 0.9.0013
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0013', 'U0013UpdateTermsofServiceLength', 'Allows 1000 characters for termsofservice column.');

ALTER TABLE systemsettings ALTER COLUMN termsofservice TYPE varchar(1000); 

