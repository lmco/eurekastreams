insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0038', 'U0038AddActivityNotifColToPersonFollower', 'add activity notification col to Follower');

ALTER TABLE ONLY Follower
    ADD COLUMN receiveNewActivityNotifications bool not null DEFAULT TRUE;
