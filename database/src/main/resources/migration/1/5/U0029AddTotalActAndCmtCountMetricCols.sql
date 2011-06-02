insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0029', 
'U0029AddTotalActAndCmtCountMetricCols', 'Add unique constraint on DailyUsageSummary');

ALTER TABLE DailyUsageSummary ADD COLUMN totalActivityCount INT NULL;
ALTER TABLE DailyUsageSummary ADD COLUMN totalCommentCount INT NULL;

