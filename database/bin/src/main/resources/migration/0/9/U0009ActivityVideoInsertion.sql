--
-- Set database version to 0.9.0009
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0009', 'U0009ActivityVideoInsertion', 'Adds activity tutorial video into the db.');

INSERT INTO tutorialvideo (version, dialogtitle, innercontent, innercontenttitle, page, videoheight, videourl, videowidth)
VALUES (1, 'Activity Overview', 'Activity Streams provide knowledge workers with an efficient way to aggregate, organize and engage in conversation around information relevant to their job.', 'Activity Streams', 'ACTIVITY', 270, 'style/videos/activity.swf', 480);