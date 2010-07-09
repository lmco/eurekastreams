--
-- Set database version to 0.9.0022
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0022', 'U0022UpdateDomainGroupMissionStatementLength', 'Reduces missionstatement column to 250 characters from 500 characters.');

UPDATE domaingroup SET missionstatement = substr(missionstatement, 1, 250);

ALTER TABLE domaingroup ALTER COLUMN missionstatement TYPE varchar(250); 

