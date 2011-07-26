insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0007', 'U0007AddGroupNotificationDisable.sql', 'Adds columns to the group to disable notifications for posting new content.');

CREATE FUNCTION Update1_0_0007() RETURNS VOID AS
$$
BEGIN
	IF NOT EXISTS(SELECT * FROM db_version WHERE major = 0 AND minor = 9 AND patch = '0039') THEN

		ALTER TABLE DomainGroup
			ADD COLUMN SuppressPostNotifToMember boolean NOT NULL DEFAULT false;
			
		ALTER TABLE DomainGroup
			ADD COLUMN SuppressPostNotifToCoordinator boolean NOT NULL DEFAULT false;

	END IF;
END;
$$
LANGUAGE 'plpgsql';

SELECT * FROM Update1_0_0007();
DROP FUNCTION Update1_0_0007();
