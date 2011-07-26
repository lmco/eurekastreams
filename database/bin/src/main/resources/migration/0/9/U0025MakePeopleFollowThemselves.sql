--
-- Set database version to 0.9.0025
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0025', 'U0025MakePeopleFollowThemselves.sql', 'Update all users to follow themselves');

-- Put into place to delete the occurances of someone already following themself.
delete from follower where followerid=followingid;

-- Given each person id, create a follower record for them to follow themself.
insert into follower (select thisid, thisid from (select id as thisid from person) as persontable);
