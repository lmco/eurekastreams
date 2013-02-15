insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0009', 'U0009AddPersonDisplayNameSuffix.sql', 'Add displayNameSuffix to Person');

ALTER TABLE Person ADD COLUMN displayNameSuffix varchar(255) NOT NULL DEFAULT '';

