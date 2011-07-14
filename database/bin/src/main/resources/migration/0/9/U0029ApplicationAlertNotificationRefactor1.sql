--
-- Set database version to 0.9.0029
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0029', 'U0029ApplicationAlertNotificationRefactor1.sql', 'Add and rename columns for application alert notifications.');

ALTER TABLE ApplicationAlertNotification 
	RENAME COLUMN activityauthorname TO auxiliaryName;

ALTER TABLE ApplicationAlertNotification 
	RENAME COLUMN destinationshortname TO destinationUniqueId;

ALTER TABLE ApplicationAlertNotification 
	ADD COLUMN auxiliaryType varchar(255),
    ADD COLUMN auxiliaryUniqueId varchar(255),
	ALTER COLUMN actorAccountId TYPE varchar(255),
	ALTER COLUMN actorName TYPE varchar(255);

UPDATE ApplicationAlertNotification
	SET 
		auxiliaryType = ac.actortype,
		auxiliaryUniqueId = ac.actorid
	FROM
		activity AS ac
	WHERE
		ApplicationAlertNotification.activityid = ac.id
		AND auxiliaryName IS NOT NULL
		AND activityid IS NOT NULL;
		