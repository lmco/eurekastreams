--
-- Set database version to 0.9.0038
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0038', 'U0038IntroVideoUpdates', 'Update tutorial videos.');

UPDATE tutorialvideo 
SET dialogtitle='Start Page', innercontenttitle='Start Page', innercontent='The start page is your own personal homepage. You can customize it with apps that display streams of activity, news feeds from both external and internal sources, or lightweight productivity applications.'
WHERE page='START'; 

UPDATE tutorialvideo 
SET dialogtitle='Profiles', innercontenttitle='Profiles', innercontent='The profiles page provides you with a quick way to browse and search for colleagues, groups and organizations based upon their profile information and organization affiliation.'
WHERE page='ORGANIZATIONS'; 

UPDATE tutorialvideo 
SET dialogtitle='Activity', innercontenttitle='Activity', innercontent='On your activity page, you can view all employee and group streams you are following and easily group and filter your streams to fit your work style.'
WHERE page='ACTIVITY'; 

UPDATE tutorialvideo 
SET dialogtitle='My Profile', innercontenttitle='My Profile', innercontent='Welcome to your personal profile. Use your profile to share information about your skills and background and your stream to post messages and links about your work-related activity.'
WHERE page='PEOPLE'; 
