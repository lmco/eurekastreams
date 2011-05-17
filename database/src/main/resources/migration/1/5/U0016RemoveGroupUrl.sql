insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0016',
'U0016RemoveGroupUrl', 'Remove url from group');

ALTER TABLE DomainGroup DROP COLUMN url; 
