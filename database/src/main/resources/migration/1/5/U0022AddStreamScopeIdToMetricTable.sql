insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0022', 
'U0022AddStreamScopeIdToMetricTable', 'Add stream scope id to metric table for streams');

ALTER TABLE UsageMetric ADD COLUMN streamviewstreamscopeid INT NULL;
