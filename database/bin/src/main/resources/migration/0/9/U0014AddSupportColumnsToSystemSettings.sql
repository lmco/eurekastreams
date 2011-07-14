--
-- Set database version to 0.9.0014
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0014', 'U0014AddSupportColumnsToSystemSettings.sql', 'Adds support columns to system settings.');
--
-- Add "supportEmailAddress" column
--

ALTER TABLE ONLY SystemSettings
    ADD COLUMN supportEmailAddress VARCHAR(255);

    
--
-- Add "supportPhoneNumber" column
--    
    
ALTER TABLE ONLY SystemSettings
    ADD COLUMN supportPhoneNumber VARCHAR(255);

    
--
-- Add "supportStreamShortName" column
--
    
ALTER TABLE ONLY SystemSettings
    ADD COLUMN supportStreamGroupShortName VARCHAR(255);