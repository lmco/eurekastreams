insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0014',
'U0014RemoveOrgFromActivity', 'Remove recipientParentOrg from Activity');

ALTER TABLE Activity DROP COLUMN recipientParentOrgid;
