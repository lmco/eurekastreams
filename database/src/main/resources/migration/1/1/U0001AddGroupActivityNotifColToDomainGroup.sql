insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0001', 'U0001AddGroupActivityNotifColToDomainGroup', 'add group activity notification col to GroupFollower');


ALTER TABLE ONLY GroupFollower
    ADD COLUMN receiveNewActivityNotifications bool not null DEFAULT TRUE;
