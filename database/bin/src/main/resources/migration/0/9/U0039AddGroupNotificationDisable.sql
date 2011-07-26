insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0039', 'U0039AddGroupNotificationDisable.sql', 'Adds columns to the group to disable notifications for posting new content.');

ALTER TABLE DomainGroup
	ADD COLUMN SuppressPostNotifToMember boolean NOT NULL DEFAULT false;
	
ALTER TABLE DomainGroup
	ADD COLUMN SuppressPostNotifToCoordinator boolean NOT NULL DEFAULT false;
