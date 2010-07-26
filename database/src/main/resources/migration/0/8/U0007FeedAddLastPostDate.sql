insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0007','U0007FeedAddLastPostDate.sql','adds lastpostdate to feed');
    
ALTER TABLE feed
ADD column lastpostdate timestamp without time zone;


