--
-- Set database version to 0.9.0035
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0035', 'U0035RemoveZipCode', 'Remove Zip Code.');

-- drop DomainGroup.privateSearchable.
UPDATE person set location=null;