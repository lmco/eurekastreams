--
-- Set database version to 0.8.0006
--

insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0006','U0006AddInitialPluginCatagory.sql','Adds Initial Plugin Catagory');

--
-- insertValues
--

insert into galleryitemcategory (version, galleryitemtype, name) values (0,'PLUGIN','Internet Services');