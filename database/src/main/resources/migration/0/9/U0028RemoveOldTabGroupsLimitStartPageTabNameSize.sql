--
-- Set database version to 0.9.0028
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0028', 'U0028RemoveOldTabGroupsLimitStartPageTabNameSize', 'Remove Tab Groups From Orgs And Groups and Limits Tab Name length form 50 to 16.');

UPDATE tabtemplate SET tabname = substr(tabname, 1, 16);

ALTER TABLE tabtemplate ALTER COLUMN tabname TYPE varchar(16); 

ALTER TABLE organization DROP CONSTRAINT fk50104153258e249e;
ALTER TABLE organization DROP CONSTRAINT fk50104153d5a4a89f;
delete FROM tab where tabgroupid IN (select id as groupid from tabgroup where id IN (select profiletabgroupid from organization));
delete FROM tabgroup where id IN (select profiletabgroupid from organization);
ALTER TABLE organization
DROP COLUMN profiletabgroupid;

ALTER TABLE domaingroup DROP CONSTRAINT fk684e33fbd5a4a89f;
ALTER TABLE domaingroup DROP CONSTRAINT fk684e33fb258e249e;
delete FROM tab where tabgroupid IN (select id as groupid from tabgroup where id IN (select profiletabgroupid from domaingroup));
delete FROM tabgroup where id IN (select profiletabgroupid from domaingroup);
ALTER TABLE domaingroup
DROP COLUMN profiletabgroupid;