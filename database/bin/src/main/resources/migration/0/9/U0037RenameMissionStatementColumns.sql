--
-- Set database version to 0.9.0037
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0037', 'U0037RenameMissionStatementColumns', 'Rename missionStatement columns');

--
-- Rename missionStatement in Org to description
--
ALTER TABLE Organization RENAME COLUMN missionStatement TO description;

--
-- Rename missionStatement in DomainGroup to description
--
ALTER TABLE DomainGroup RENAME COLUMN missionStatement TO description;

--
-- Rename quote in Person to jobDescription
--
ALTER TABLE Person RENAME COLUMN quote TO jobDescription;
