--
-- Set database version to 1.0.0009
--

insert into db_version (major, minor, patch, scriptname, description) values (1, 0, '0009', 'U0009RemoveStreamView.sql', 'Remove stream view');

-- Remove StreamView and StreamSearch
ALTER TABLE DomainGroup DROP CONSTRAINT fk684e33fbc9f1fe30;
ALTER TABLE Person DROP CONSTRAINT fk8e488775a5396b4f;
ALTER TABLE Organization DROP CONSTRAINT fk50104153a5396b4f;
ALTER TABLE StreamSearch DROP CONSTRAINT fkb3a655689da9b48b;

ALTER TABLE DomainGroup DROP COLUMN entitystreamviewid;
ALTER TABLE Person DROP COLUMN entitystreamviewid;
ALTER TABLE Organization DROP COLUMN entitystreamviewid;

DROP TABLE StreamView_StreamScope;
DROP TABLE Person_StreamView;
DROP TABLE StreamView;

DROP TABLE StreamSearch_Keywords;
DROP TABLE Person_StreamSearch;
DROP TABLE StreamSearch;


-- Make StreamScope.destinationEntityId non-transient
ALTER TABLE StreamScope ADD COLUMN destinationEntityId bigint;

UPDATE StreamScope SET destinationEntityId = o.id FROM Organization o WHERE StreamScope.scopetype = 'ORGANIZATION' AND StreamScope.uniquekey = o.shortName;  
UPDATE StreamScope SET destinationEntityId = p.id FROM Person p WHERE StreamScope.scopetype = 'PERSON' AND StreamScope.uniquekey = p.accountId;
UPDATE StreamScope SET destinationEntityId = g.id FROM DomainGroup g WHERE StreamScope.scopetype = 'GROUP' AND StreamScope.uniquekey = g.shortName;  
