insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0020',
'U0020RemovePersonGroupOrdering', 'Remove ordering of persons groups');

ALTER TABLE GroupFollower DROP COLUMN groupstreamindex;
