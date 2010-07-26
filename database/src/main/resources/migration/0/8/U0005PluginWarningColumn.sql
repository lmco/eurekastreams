--
-- Set database version to 0.8.0005
--

insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0005','U0005PluginWarningColumn.sql','Adds pluginwarning column');

--
-- Alter tables
--

ALTER TABLE systemsettings ADD column pluginwarning character varying(255);