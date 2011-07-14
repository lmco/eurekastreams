--
-- Set database version to 0.9.0006
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0006', 'U0006GroupStreamUpdates.sql', 'updates person table to add a groupStreamHiddenLineIndex column.');

ALTER TABLE person ADD COLUMN groupstreamhiddenlineindex integer NOT NULL DEFAULT 3;

ALTER TABLE groupfollower ADD COLUMN groupstreamindex integer;

-- loop over existing groupfollower rows and assign a default groupstreamindex, starting with 0
create function setgroupstreamindex() returns VOID as
$$
declare
  followers RECORD;
  groups RECORD;
  groupstreamindexcount integer;
begin

for followers in select distinct followerid from groupfollower loop
   groupstreamindexcount := 0;
   for groups in select followingid from groupfollower where followerid = followers.followerid order by followingid loop
     update groupfollower set groupstreamindex = groupstreamindexcount where followerid = followers.followerid and followingid = groups.followingid;
     groupstreamindexcount := groupstreamindexcount + 1;
   end loop;
end loop;

end;
$$
language 'plpgsql';

select * from setgroupstreamindex();
drop function setgroupstreamindex();