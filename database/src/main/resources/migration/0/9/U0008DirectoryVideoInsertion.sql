--
-- Set database version to 0.9.0008
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0008', 'U0008DirectoryVideoInsertion', 'Adds directory tutorial video into the db.');

INSERT INTO tutorialvideo (version, dialogtitle, innercontent, innercontenttitle, page, videoheight, videourl, videowidth)
VALUES (1, 'Directory Overview', 'The Directory provides knowledge workers with a quick way to browse and search for colleagues, groups and organizations based on profile information and trending activity.', 'Directory', 'ORGANIZATIONS', 270, 'style/videos/profiles.swf', 480);