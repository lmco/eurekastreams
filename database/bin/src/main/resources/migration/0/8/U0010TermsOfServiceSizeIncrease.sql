--
-- Set database version to 0.8.0010
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 8, '0010', 'U0010TermsOfServiceSizeIncrease.sql', 'updates systemsettings table to increase column size for terms of service text.');

ALTER TABLE systemsettings ALTER COLUMN termsofservice SET DATA TYPE character varying(1000);
