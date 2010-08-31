--
-- Set database version to 1.0.0001
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0001', 'U0001LikedActivityTable.sql', 'Creates the liked activity table.');

CREATE TABLE likedactivity (
    activityid bigint NOT NULL,
    personid bigint NOT NULL
);
