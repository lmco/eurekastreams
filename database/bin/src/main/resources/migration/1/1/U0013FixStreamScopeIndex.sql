insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0013', 
'U0013FixStreamScopeIndex', 'Fix stream scope index');

CREATE INDEX streamscope_scopeType_uniquekey_idx
  ON streamscope
  USING btree 
  (scopeType, uniquekey);
