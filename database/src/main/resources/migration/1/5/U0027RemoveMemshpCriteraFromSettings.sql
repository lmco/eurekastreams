insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0027', 
'U0027RemoveMemshpCriteraFromSettings', 'Remove association between Membership criteria and System Settings.');

ALTER TABLE membershipcriteria DROP CONSTRAINT fk30d3517568a7e2e9;

ALTER TABLE membershipcriteria DROP COLUMN systemsettingsid;

