insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0015',
'U0015RemoveOrganizationTables', 'Remove organization tables');

drop table IF EXISTS Message;
drop table organization_capability;
drop table organization;
