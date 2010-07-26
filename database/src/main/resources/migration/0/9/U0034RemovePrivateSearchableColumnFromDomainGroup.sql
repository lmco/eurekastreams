--
-- Set database version to 0.9.0034
--

insert into db_version (major, minor, patch, scriptname, description) values (0, 9, '0034', 'U0034RemovePrivateSearchableColumnFromDomainGroup', 'Remove DomainGroup.privateSearchable.');

-- drop DomainGroup.privateSearchable.
ALTER TABLE domaingroup DROP COLUMN privatesearchable;