insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0001', 
'U0001RemoveRelatedOrgAndMovePeopleToRootOrg', 'Delete related orgs from people, move people/groups to root org');

UPDATE Person SET parentOrganizationId = (SELECT id FROM organization WHERE parentOrganizationId = id);

DROP TABLE Person_RelatedOrganization;
