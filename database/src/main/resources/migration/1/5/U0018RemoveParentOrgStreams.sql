insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0018',
'U0018RemoveParentOrgStreams', 'Remove parent org streams');

ALTER TABLE Person_Stream DROP COLUMN streamindex;
DELETE FROM Person_Stream where streamId IN
     (SELECT id FROM Stream where name='EUREKA:PARENT_ORG_TAG' AND readonly=true);
DELETE FROM Stream where name='EUREKA:PARENT_ORG_TAG' AND readonly=true;
