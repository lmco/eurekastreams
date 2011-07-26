insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0045', 'U0045CascadePersonDelete', 'Set up foreign keys to person to allow cascading deletes.');

ALTER TABLE inappnotification ADD CONSTRAINT inappnotification_recipientid_fkey FOREIGN KEY (recipientid) REFERENCES person (id) ON DELETE CASCADE;
ALTER TABLE follower ADD CONSTRAINT follower_followingid_fkey FOREIGN KEY (followingid) REFERENCES person (id) ON DELETE CASCADE;
ALTER TABLE follower ADD CONSTRAINT follower_followerid_fkey FOREIGN KEY (followerid) REFERENCES person (id) ON DELETE CASCADE;

ALTER TABLE inappnotification DROP CONSTRAINT fk157489a7880d76c6;
ALTER TABLE follower DROP CONSTRAINT fk15d7843e52a2bc28;
ALTER TABLE follower DROP CONSTRAINT fk15d7843e90f5b78b;
