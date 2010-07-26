--
-- Set database version to 0.9.0003
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0003', 'U0003ApplicationAlertNotificationAddTimestamp.sql', 'updates notification table to add a timestamp column.');

ALTER TABLE applicationalertnotification ADD COLUMN activitytype character varying(255);

ALTER TABLE applicationalertnotification ADD COLUMN isread boolean NOT NULL DEFAULT false;

ALTER TABLE applicationalertnotification ADD COLUMN groupname character varying(255);

ALTER TABLE applicationalertnotification ADD COLUMN groupshortname character varying(255);

ALTER TABLE applicationalertnotification ADD COLUMN notificationdate timestamp without time zone NOT NULL DEFAULT current_timestamp;
