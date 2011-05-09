insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0003', 
'U0003RemoveNonRootOrgCoords', 'Remove org coordinators from non-root orgs');

DELETE FROM organization_coordinators where organization_id NOT IN (
    SELECT id FROM Organization WHERE parentOrganizationId = id
);
