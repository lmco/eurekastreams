insert into db_version (major, minor, patch, scriptname, description) values (1, 5, '0030', 
'U0030AddTotalContributorCountToMetric', 'Add total contributor count on DailyUsageSummary');

ALTER TABLE DailyUsageSummary ADD COLUMN totalStreamViewCount INT NULL;
ALTER TABLE DailyUsageSummary ADD COLUMN totalContributorCount INT NULL;
