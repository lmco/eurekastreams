insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0016',
'U0016RemoveGroupUrl', 'Remove url from group');

-- NOTE: This shouldn't have been removed - it'll be conditionally put back in a later change script for developers
-- ALTER TABLE DomainGroup DROP COLUMN url; 
