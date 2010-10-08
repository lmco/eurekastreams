insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0013', 'U0013AddBrokenAndGUIDFeedFields.sql', 'Adds columns to the feed table.');

ALTER TABLE Feed
    ADD COLUMN lastSeenGUID character varying(255);
    
    
ALTER TABLE Feed
    ADD COLUMN broken boolean;