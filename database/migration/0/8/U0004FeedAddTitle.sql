insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0004','U0004FeedAddTitle.sql','adds title to feed');
    
ALTER TABLE feed
ADD title character varying(255) NOT NULL;


