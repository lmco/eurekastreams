insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0005', 'U0005RemoveResourceTypeColFromSharedResource', 'remove col from sharedresource table');

ALTER TABLE SharedResource DROP COLUMN resourceType;

ALTER TABLE SharedResource ADD CONSTRAINT KEY_SharedResource_uniqueKey UNIQUE(uniqueKey);
