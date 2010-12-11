--
-- Set database version to 0.8.0009
--

insert into db_version (major, minor, patch, scriptname, description)
    values (0,8,'0009','U0009AddDefaultPlugins.sql','Adds the default stream plugins');

--
-- insertValues
--

insert into plugindefinition (version, created, url, uuid, objecttype, plugincategoryid, ownerid, showingallery, numberofusers, updatefrequency)
	values
	(0, now(), 'http://localhost:8080/plugins/delicious.xml', '90c18fdb-ed11-4ac4-8da0-16f0d2ab4d69', 'BOOKMARK', 7, null, true, 0, 30),
	(0, now(), 'http://localhost:8080/plugins/googlereader.xml', 'c2d7f1e2-d427-476d-8616-b28f7392153c', 'BOOKMARK', 7, null, true, 0, 30),
	(0, now(), 'http://localhost:8080/plugins/rss.xml', '099f6d93-4fb4-41ec-a690-0762f5a34311', 'BOOKMARK', 7, null, true, 0, 30),
	(0, now(), 'http://localhost:8080/plugins/youtube.xml', '93710e05-9f60-4244-add6-310e03872741', 'VIDEO', 7, null, true, 0, 30);
