--
-- Set database version to 0.9.0021
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0021', 'U0021RemoveBannerBackgroundColor.sql', 'Removes banner background color from groups and orgs.');


ALTER TABLE domaingroup DROP COLUMN bannerbackgroundcolor;
ALTER TABLE organization DROP COLUMN bannerbackgroundcolor;
