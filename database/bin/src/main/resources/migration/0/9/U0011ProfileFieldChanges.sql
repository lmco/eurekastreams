--
-- Set database version to 0.9.0011
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0011', 'U0011ProfileFieldChanges.sql', 'Updates several columns used in the profile.');

ALTER TABLE person 
	ALTER COLUMN quote TYPE varchar(250),
	ALTER COLUMN biography TYPE text;

ALTER TABLE job
	ALTER COLUMN description TYPE text;
 	
ALTER TABLE enrollment
	ALTER COLUMN additionaldetails TYPE text;
