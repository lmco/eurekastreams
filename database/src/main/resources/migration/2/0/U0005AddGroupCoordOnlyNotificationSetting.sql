insert into db_version (major, minor, patch, scriptname, description) values (2, 0, '0005', 'U0005AddGroupCoordOnlyNotificationSetting.sql', 'Adds a setting to group follower table to limit notification subscriptions.');


ALTER TABLE GroupFollower ADD COLUMN coordinatorOnlyNotifications bool NOT NULL DEFAULT false;