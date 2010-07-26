--
-- Set database version to 0.9.0036
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0036', 'U0036LowercaseAllGroups', 'Make all DomainGroup shortnames lowercase.');

-- lowercase all shortnames in the domaingroup table
 UPDATE domaingroup SET shortname = LOWER(shortname);
 
-- lowercase all uniquekeys in streamscope
UPDATE streamscope SET uniquekey=LOWER(uniquekey);

-- lowercase all destinationuniqueid in aplicationalertnotification
UPDATE applicationalertnotification SET destinationuniqueid=LOWER(destinationuniqueid);

-- lowercase all shortnames in the organization table
UPDATE organization SET shortname=LOWER(shortname);