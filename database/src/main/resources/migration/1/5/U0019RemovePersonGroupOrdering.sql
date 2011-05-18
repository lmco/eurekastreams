insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0019',
'U0019RemovePersonGroupOrdering', 'Remove ordering of persons groups');

ALTER TABLE GroupFollower DROP COLUMN groupstreamindex;
