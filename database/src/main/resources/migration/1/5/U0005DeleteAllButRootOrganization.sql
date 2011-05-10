insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0005', 
'U0005DeleteAllButRootOrganization', 'Delete all orgs except the root organization');

DELETE FROM Organization WHERE parentOrganizationId <> id;

