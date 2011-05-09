insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0002', 
'U0002MigrateGroupsToRootOrg', 'Move all groups to Root Org');

UPDATE DomainGroup SET parentOrganizationId = (SELECT id FROM organization WHERE parentOrganizationId = id);
