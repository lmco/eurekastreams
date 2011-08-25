insert into db_version (major, minor, patch, scriptname, description) values (1, 6, '0001', 'U0001DropGroupNotificationSuppressionColumns', 'Drop obsolete group notification suppression columns');

ALTER TABLE domaingroup DROP COLUMN suppresspostnotiftomember;
ALTER TABLE domaingroup DROP COLUMN suppresspostnotiftocoordinator;
