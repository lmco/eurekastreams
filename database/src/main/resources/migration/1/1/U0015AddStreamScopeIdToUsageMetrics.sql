insert into db_version (major, minor, patch, scriptname, description) values (1, 1, '0015', 
'U0015AddStreamScopeIdToUsageMetrics', 'Add stream scope id to metric table for streams');

ALTER TABLE UsageMetric ADD COLUMN streamviewstreamscopeid INT NULL;
