--
-- Set database version to 0.9.0007
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0007', 'U0007StartVideoInsertion', 'Adds start page tutorial video into the db.');

INSERT INTO tutorialvideo (version, dialogtitle, innercontent, innercontenttitle, page, videoheight, videourl, videowidth)
VALUES (1, 'Start Page Overview', 'The start page provides knowledge workers with a private home page they can theme and customize to display news and streams of activity.', 'Start Page', 'START', 270, 'style/videos/start.swf', 480);
