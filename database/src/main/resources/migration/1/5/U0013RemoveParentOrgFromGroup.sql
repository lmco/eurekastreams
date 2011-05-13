insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0013',
'U0013RemoveParentOrgFromGroup', 'Remove parent org from group');

ALTER TABLE DomainGroup DROP COLUMN parentorganizationid; 
