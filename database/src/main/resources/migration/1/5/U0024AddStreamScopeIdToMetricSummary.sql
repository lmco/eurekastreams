insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0024', 
'U0024AddStreamScopeIdToMetricSummary', 'Add stream scope id to metric summary table');

ALTER TABLE DailyUsageSummary ADD COLUMN streamViewStreamScopeId INT NULL;