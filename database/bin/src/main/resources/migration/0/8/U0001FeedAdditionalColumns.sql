--
-- Set database version to 0.8.0001
--

insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0001','U0001FeedAdditionalColumns.sql','Creates group_feed and person_feed tables');

--
-- Alter tables
--

ALTER TABLE ONLY feed
    ADD CONSTRAINT feed_pkey PRIMARY KEY (id);
