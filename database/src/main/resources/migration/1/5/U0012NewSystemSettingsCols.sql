insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0012',
'U0012NewSystemSettingsCols', 'Added new system settings cols');

ALTER TABLE SystemSettings ADD COLUMN allUsersCanCreateGroups bool null;
UPDATE SystemSettings SET allUsersCanCreateGroups=false;
ALTER TABLE SystemSettings ALTER COLUMN allUsersCanCreateGroups SET NOT NULL;

