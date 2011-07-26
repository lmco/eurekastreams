--
-- Set database version to 0.9.0032
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0032', 'U0032IntroVideoUpdates', 'Update tutorial videos.');

UPDATE tutorialvideo 
SET dialogtitle='Profile Overview', innercontenttitle='Profiles', innercontent='Profiles provide knowledge workers with a quick way to browse and search for colleagues, groups and organizations based on profile information.'
WHERE page='ORGANIZATIONS'; 

UPDATE tutorialvideo 
SET innercontent='Your profile allows you to capture profile information including work history, education, interest, skills and hobbies.  You are also able to upload an avatar for your stream.'
WHERE page='PEOPLE';