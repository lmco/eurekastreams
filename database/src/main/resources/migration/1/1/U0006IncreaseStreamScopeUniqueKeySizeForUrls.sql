insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0006', 'U0006IncreaseStreamScopeUniqueKeySizeForUrls', 'incrase streamscope unique key size');

ALTER TABLE StreamScope ALTER COLUMN uniqueKey type varchar(2000);