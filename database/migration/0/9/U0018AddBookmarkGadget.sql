--
-- Set database version to 0.9.0018
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0018', 'U0018AddBookmarkGadget.sql', 'Adds the bookmark gadget entry into the gadget definition table.');

INSERT INTO gadgetdefinition(version, created, url, uuid, gadgetcategoryid, ownerid, showingallery, numberofusers) VALUES (0, '2010-04-30', 'http://localhost:8080/org/eurekastreams/gadgets/bookmarkgadget.xml','D9DA7754-5495-11DF-B12E-0806DFD72085', 1, null, true, 0);
