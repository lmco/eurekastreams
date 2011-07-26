--
-- Set database version to 0.9.0010
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0010', 'U0010MyProfileVideoInsertion', 'Adds My Profile tutorial video into the db.');

INSERT INTO tutorialvideo (version, dialogtitle, innercontent, innercontenttitle, page, videoheight, videourl, videowidth)
VALUES (1, 'Profile Overview', 'Your profile allows you to capture profile information including work history, education, colleague recommendations, interest, skills and hobbies.  You are also able to upload an avatar for your stream.', 'My Profile', 'PEOPLE', 270, 'style/videos/myProfile.swf', 480);