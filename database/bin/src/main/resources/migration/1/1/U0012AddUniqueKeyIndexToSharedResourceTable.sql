insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0012', 
'U0012AddUniqueKeyIndexToSharedResourceTable', 'Add index on shared resource unique key');

CREATE INDEX sharedresource_uniquekey_idx
  ON sharedresource
  USING btree
  (uniquekey);
