insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0011', 
'U0011RemoveOrgFromPersonTable', 'Unhook organizations from different entities');

ALTER TABLE Person DROP COLUMN parentorganizationid;
