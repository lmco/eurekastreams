insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0004', 
'U0004MoveActivitiesToRootOrg', 'Move all activities to root org');

UPDATE Activity SET recipientParentOrgId = (SELECT id FROM Organization WHERE parentOrganizationId = id);
