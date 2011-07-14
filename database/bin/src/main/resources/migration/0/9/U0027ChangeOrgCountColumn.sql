--
-- Set database version to 0.9.0027
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0027', 'U0027ChangeOrgCountColumn.sql', 'Change the descendant org count column to an immediate child count column.');

ALTER TABLE organization RENAME COLUMN descendantorganizationcount TO childorganizationcount;

UPDATE organization as o SET childorganizationcount = (SELECT COUNT(*) FROM organization as o2 WHERE o.id = o2.parentorganizationid and o2.parentorganizationid <> o2.id);
