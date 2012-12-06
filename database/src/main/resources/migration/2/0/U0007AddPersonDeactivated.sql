insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0007', 'U0007AddPersonDeactivated.sql', 'Adds the deactivated column to Person to allow forced locking of a user.');

ALTER TABLE Person ADD COLUMN accountDeactivated boolean NOT NULL DEFAULT false;
